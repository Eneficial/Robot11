
package Team4450.Robot11;

import java.lang.Math;

import Team4450.Lib.*;
import Team4450.Lib.JoyStick.*;
import Team4450.Lib.LaunchPad.*;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.DriverStation.MatchType;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

class Teleop
{
	private final Robot 				robot;
	private final CubeManipulation	 	Cube;
	private final GearBox 				gearBox;
	private final Climber				Climber;
	
	public  JoyStick			rightStick, leftStick, utilityStick;
	public  LaunchPad			launchPad;
	private boolean				autoTarget, invertDrive, altDriveMode;
	private Vision				vision;

	// Constructor.

	Teleop(Robot robot)
	{
		Util.consoleLog();

		this.robot = robot;
		Cube = new CubeManipulation(robot);
		gearBox = new GearBox(robot);
		Climber = new Climber(robot);

		vision = Vision.getInstance(robot);
	}

	// Free all objects that need it.

	void dispose()
	{
		Util.consoleLog();
		if (leftStick != null) leftStick.dispose();
		if (rightStick != null) rightStick.dispose();
		if (utilityStick != null) utilityStick.dispose();
		if (launchPad != null) launchPad.dispose();
		if (gearBox != null) gearBox.dispose();
		if (Climber != null) Climber.dispose();
		if (Cube != null) {
			Cube.CubeStop();
			Cube.dispose();
		}
	}

	void OperatorControl()
	{
		double	rightY = 0, leftY = 0, utilY = 0, rightX = 0, leftX = 0;
		double	gain = .01;
		boolean	steeringAssistMode = false;
		int		angle;

		// Motor safety turned off during initialization.
		Devices.robotDrive.setSafetyEnabled(false);
		Climber.servoReset();
		Climber.winchBraker(false);

		Devices.SetCANTalonBrakeMode(false);
		
		Util.consoleLog();

		LCD.printLine(1, "Mode: OperatorControl");
		LCD.printLine(2, "All=%s, Start=%d, FMS=%b", robot.alliance.name(), robot.location, Devices.ds.isFMSAttached());

		// Configure LaunchPad and Joystick event handlers.

		launchPad = new LaunchPad(Devices.launchPad, LaunchPadControlIDs.BUTTON_BLUE, this);

		LaunchPadControl lpControl = launchPad.AddControl(LaunchPadControlIDs.ROCKER_LEFT_BACK);
		lpControl.controlType = LaunchPadControlTypes.SWITCH;

		lpControl = launchPad.AddControl(LaunchPadControlIDs.ROCKER_LEFT_FRONT);
		lpControl.controlType = LaunchPadControlTypes.SWITCH;
		
		lpControl = launchPad.AddControl(LaunchPadControlIDs.ROCKER_RIGHT);
		lpControl.controlType = LaunchPadControlTypes.SWITCH;


		//Example on how to track button:
		//launchPad.AddControl(LaunchPadControlIDs.BUTTON_COLOR_HERE);
		launchPad.AddControl(LaunchPadControlIDs.BUTTON_BLUE);
		launchPad.AddControl(LaunchPadControlIDs.BUTTON_YELLOW);
		//launchPad.AddControl(LaunchPadControlIDs.BUTTON_RED);
		launchPad.AddControl(LaunchPadControlIDs.BUTTON_RED_RIGHT);
		launchPad.AddControl(LaunchPadControlIDs.BUTTON_BLUE_RIGHT);
		launchPad.AddControl(LaunchPadControlIDs.BUTTON_GREEN);
		launchPad.AddControl(LaunchPadControlIDs.BUTTON_BLACK);
		launchPad.AddControl(LaunchPadControlIDs.ROCKER_LEFT_BACK);
		
		launchPad.addLaunchPadEventListener(new LaunchPadListener());
		launchPad.Start();

		leftStick = new JoyStick(Devices.leftStick, "LeftStick", JoyStickButtonIDs.TRIGGER, this);
		//Example on how to track button:
		//leftStick.AddButton(JoyStickButtonIDs.BUTTON_NAME_HERE);
		leftStick.addJoyStickEventListener(new LeftStickListener());
		leftStick.Start();

		rightStick = new JoyStick(Devices.rightStick, "RightStick", JoyStickButtonIDs.TRIGGER, this);
		//Example on how to track button:
		//rightStick.AddButton(JoyStickButtonIDs.BUTTON_NAME_HERE);
		rightStick.AddButton(JoyStickButtonIDs.TOP_BACK);
		rightStick.AddButton(JoyStickButtonIDs.TOP_LEFT);
		rightStick.AddButton(JoyStickButtonIDs.TOP_RIGHT);
		rightStick.addJoyStickEventListener(new RightStickListener());
		rightStick.Start();

		utilityStick = new JoyStick(Devices.utilityStick, "UtilityStick", JoyStickButtonIDs.TRIGGER, this);
		//Example on how to track button:
		//utilityStick.AddButton(JoyStickButtonIDs.BUTTON_NAME_HERE);
		utilityStick.AddButton(JoyStickButtonIDs.TRIGGER);
		utilityStick.AddButton(JoyStickButtonIDs.TOP_MIDDLE);
		utilityStick.AddButton(JoyStickButtonIDs.TOP_BACK);
		utilityStick.AddButton(JoyStickButtonIDs.TOP_LEFT);
		utilityStick.AddButton(JoyStickButtonIDs.TOP_RIGHT);
	
		utilityStick.addJoyStickEventListener(new UtilityStickListener());
		utilityStick.Start();

		// Tighten up dead zone for smoother climber movement.
		utilityStick.deadZone = 0.15;

		// Set CAN Talon brake mode by rocker switch setting.
		// We do this here so that the Utility stick thread has time to read the initial state
		// of the rocker switch.
		if (robot.isComp) Devices.SetCANTalonBrakeMode(lpControl.latchedState);

		// Set gyro/Navx to heading 0.
		//robot.gyro.reset();
		Devices.navx.resetYaw();

		Devices.navx.setHeading(90);

		// Reset encoder.
		Devices.encoder1.reset();
		Devices.encoder2.reset();

		// Motor safety turned on.
		Devices.robotDrive.setSafetyEnabled(true);

		// Driving loop runs until teleop is over.

		while (robot.isEnabled() && robot.isOperatorControl())
		{
			// Get joystick deflection and feed to robot drive object
			// using calls to our JoyStick class.

			rightY = stickLogCorrection(rightStick.GetY());	// fwd/back
			leftY = stickLogCorrection(leftStick.GetY());	// fwd/back

			rightX = stickLogCorrection(rightStick.GetX());	// left/right
			leftX = stickLogCorrection(leftStick.GetX());	// left/right

			utilY = utilityStick.GetY();

			LCD.printLine(3, "limit switch= %b", Devices.winchSwitcher.get());
			LCD.printLine(4, "leftY=%.4f  rightY=%.4f  utilY=%.4f", leftY, rightY, utilY);
			LCD.printLine(6, "yaw=%.2f, adj yaw=%.2f, total=%.2f, rate=%.2f, hdng=%.2f", Devices.navx.getYaw(), adjustAngle(Devices.navx.getYaw()), Devices.navx.getTotalYaw(), 
					Devices.navx.getYawRate(), Devices.navx.getHeading());
			LCD.printLine(7, "winchEncoder=%d", Devices.winchEncoder.get());
			LCD.printLine(8, "pressureV=%.2f  psi=%d", robot.monitorCompressorThread.getVoltage(), robot.monitorCompressorThread.getPressure());
			LCD.printLine(9, "Drive Encoders: 1:%d 2:%d", Devices.encoder1.get(), Devices.encoder2.get());

			// Set wheel motors.
			// Do not feed JS input to robotDrive if we are controlling the motors in automatic functions.

			//if (!autoTarget) robot.robotDrive.tankDrive(leftY, rightY);

			// Two drive modes, full tank and alternate. Switch on right stick trigger.

			if (!autoTarget) 
			{
				if (altDriveMode)
				{	// normal tank with straight drive assist when sticks within 10% of each other.
					if (leftRightEqual(leftY, rightY, 10) && Math.abs(rightY) > .50)
					{
						if (!steeringAssistMode) Devices.navx.resetYaw();

						// Angle is negative if robot veering left, positive if veering right when going forward.
						// It is opposite when going backward. Note that for this robot, - power means forward and
						// + power means backward.

						angle = (int) Devices.navx.getYaw();

						LCD.printLine(5, "angle=%d", angle);

						// Invert angle for backwards.

						if (rightY > 0) angle = -angle;

						//Util.consoleLog("angle=%d", angle);

						// Note we invert sign on the angle because we want the robot to turn in the opposite
						// direction than it is currently going to correct it. So a + angle says robot is veering
						// right so we set the turn value to - because - is a turn left which corrects our right
						// drift.

						Devices.robotDrive.curvatureDrive(rightY, angle * gain, true);

						steeringAssistMode = true;
					}
					else
					{
						steeringAssistMode = false;
						Devices.robotDrive.tankDrive(leftY, rightY);		// Normal tank drive.
					}

					SmartDashboard.putBoolean("Overload", steeringAssistMode);
				}
				else
					Devices.robotDrive.tankDrive(leftY, rightY);		// Normal tank drive.
			}
			
			Cube.powerControl(utilityStick.GetY());

			// Update the robot heading indicator on the DS.

			SmartDashboard.putNumber("Gyro", Devices.navx.getHeading());

			// End of driving loop.

			Timer.delay(.020);	// wait 20ms for update from driver station.
		}

		// End of teleop mode.

		Util.consoleLog("end");
	}

	private boolean leftRightEqual(double left, double right, double percent)
	{
		//if (left == right) return true;

		if (Math.abs(left - right) <= (1 * (percent / 100))) return true;

		return false;
	}

	// Custom base logarithm.
	// Returns logarithm base of the value.

	private double baseLog(double base, double value)
	{
		return Math.log(value) / Math.log(base);
	}

	// Map joystick y value of 0.0 to 1.0 to the motor working power range of approx 0.5 to 1.0 using
	// logarithmic curve.

	private double stickLogCorrection(double joystickValue)
	{
		double base = Math.pow(2, 1/3) + Math.pow(2, 1/3);

		if (joystickValue > 0)
			joystickValue = baseLog(base, joystickValue + 1);
		else if (joystickValue < 0)
			joystickValue = -baseLog(base, -joystickValue + 1);

		return joystickValue;
	}

	

	// Handle LaunchPad control events.

	public class LaunchPadListener implements LaunchPadEventListener 
	{
		public void ButtonDown(LaunchPadEvent launchPadEvent) 
		{
			LaunchPadControl	control = launchPadEvent.control;

			Util.consoleLog("%s, latchedState=%b", control.id.name(),  control.latchedState);

			switch(control.id)
			{
			
			case BUTTON_BLUE: //Forklift drops
				if (launchPadEvent.control.latchedState)
					//Climber.armExtend(); //fix this
				break;
				
				case BUTTON_RED_RIGHT: //Open and close wrist - Toggle wrist folding in or out.
					if (launchPadEvent.control.latchedState) 
						Cube.cubeWristIn(); 
					else
						Cube.cubeWristOut();
				break;
					
			case BUTTON_RED: //Gear shifting
				if (launchPadEvent.control.latchedState)
					gearBox.highGear();
				else
					gearBox.lowGear();
				break;
					
			case BUTTON_BLUE_RIGHT: //Cube intake
				if (launchPadEvent.control.latchedState)
					Cube.autoIntakeStart();
				break;
					
			case BUTTON_YELLOW: //Turns the break on.
				if (Devices.ds.getMatchTime() <= 45) 
					Climber.winchBraker(control.latchedState);
				break;
				
			case BUTTON_GREEN: //Resets encoders
				Devices.encoder1.reset();
				Devices.encoder2.reset();
				break;
				
				
			default:
				Util.consoleLog("Unassigned button pressed: " + control.id.name());
				break;
			}
		}

		public void ButtonUp(LaunchPadEvent launchPadEvent) 
		{
			//Util.consoleLog("%s, latchedState=%b", launchPadEvent.control.name(),  launchPadEvent.control.latchedState);
		}

		public void SwitchChange(LaunchPadEvent launchPadEvent) 
		{
			LaunchPadControl	control = launchPadEvent.control;

			Util.consoleLog("%s", control.id.name());

			switch(control.id)
			{
			
				case ROCKER_LEFT_FRONT: //Camera changer
						robot.cameraThread.ChangeCamera();
					break;
					
				case ROCKER_LEFT_BACK: //Brake mode toggle
					Devices.SetCANTalonBrakeMode(control.latchedState);
			
				default:
					break;
			}
		}
	}

	// Handle Right JoyStick Button events.

	private class RightStickListener implements JoyStickEventListener 
	{

		public void ButtonDown(JoyStickEvent joyStickEvent) 
		{
			JoyStickButton	button = joyStickEvent.button;

			Util.consoleLog("%s, latchedState=%b", button.id.name(),  button.latchedState);

			switch(button.id)
			{
			case TRIGGER:
				altDriveMode = !altDriveMode;
				break;
				
			case TOP_RIGHT: //Extend pin
				if (Devices.ds.getMatchTime() <= 45 || Devices.ds.getMatchType() == MatchType.None)
					Climber.pinActuator(0.5);
				break;
			case TOP_BACK: //Retract pin
				if (Devices.ds.getMatchTime() <= 45 || Devices.ds.getMatchType() == MatchType.None)
					Climber.pinActuator(0.2);
				break;
				
			//Example of Joystick Button case:
			/*
			case BUTTON_NAME_HERE:
				if (button.latchedState)
					DoOneThing();
				else
					DoOtherThing();
				break;
			 */
			default:
				break;
			}
		}

		public void ButtonUp(JoyStickEvent joyStickEvent) 
		{
			//Util.consoleLog("%s", joyStickEvent.button.name());
		}
	}

	// Handle Left JoyStick Button events.

	private class LeftStickListener implements JoyStickEventListener 
	{
		public void ButtonDown(JoyStickEvent joyStickEvent) 
		{
			JoyStickButton	button = joyStickEvent.button;

			Util.consoleLog("%s, latchedState=%b", button.id.name(),  button.latchedState);

			switch(button.id)
			{
			//Example of Joystick Button case:
			
			case TRIGGER:
				if (button.latchedState)
					gearBox.highGear();
				else
					gearBox.lowGear();
				break;
			 
			default:
				break;
			}
		}

		public void ButtonUp(JoyStickEvent joyStickEvent) 
		{
			//Util.consoleLog("%s", joyStickEvent.button.name());
		}
	}

	// Handle Utility JoyStick Button events.

	private class UtilityStickListener implements JoyStickEventListener 
	{
		public void ButtonDown(JoyStickEvent joyStickEvent) 
		{
			JoyStickButton	button = joyStickEvent.button;

			Util.consoleLog("%s, latchedState=%b", button.id.name(),  button.latchedState);

			switch(button.id)
			{
			case TRIGGER: //Open and close wrist(?)
				if (button.latchedState)
					Cube.cubeOpen();
				else
					Cube.cubeClose();
					break;
					
			case TOP_MIDDLE: //Manually toggle Claw motors in ejecting direction.
				if (button.latchedState)
					Cube.CubeOuttake(0.50);
				else
					Cube.CubeStop();
				break;
				
			case TOP_BACK: //Manually toggle Claw motors in the intaking direction.
				if (button.latchedState)
					Cube.CubeIntake(0.50);
				else
					Cube.CubeStop();
				break;
				
			case TOP_RIGHT:
				if (button.latchedState)
					Cube.autoIntakeStart();
				else
					Cube.autoIntakeStop();
				break;
			default:
				break;
			}
		}

		public void ButtonUp(JoyStickEvent joyStickEvent) 
		{
			//Util.consoleLog("%s", joyStickEvent.button.id.name());
		}
	}
	
	public float adjustAngle(float angle) {
		if (Robot.isClone) return angle*(18.0f/15.0f);
		else return angle;
	}
}
