
package Team4450.Robot11;

import Team4450.Lib.*;
import Team4450.Robot11.Devices;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Autonomous
{
	private final Robot	robot;
	private final int	program = (int) SmartDashboard.getNumber("AutoProgramSelect",0);
	
	private final CubeManipulation Cube;
	private final GearBox gearBox;
	
	Autonomous(Robot robot)
	{
		Util.consoleLog();
		
		this.robot = robot;		
		Cube = new CubeManipulation(robot); 
		gearBox = new GearBox(robot); 
	}

	public void dispose()
	{
		Util.consoleLog();
		if (gearBox != null) gearBox.dispose();
		if (Cube != null) Cube.dispose();
	}
	
	private boolean isAutoActive()
	{
		return robot.isEnabled() && robot.isAutonomous();
	}

	public void execute()
	{
		Util.consoleLog("Alliance=%s, Location=%d, Program=%d, FMS=%b, msg=%s", robot.alliance.name(), robot.location, program, 
				Devices.ds.isFMSAttached(), robot.gameMessage);
		LCD.printLine(2, "Alliance=%s, Location=%d, FMS=%b, Program=%d, msg=%s", robot.alliance.name(), robot.location, 
				Devices.ds.isFMSAttached(), program, robot.gameMessage);

		Devices.robotDrive.setSafetyEnabled(false);

		//TODO Encoder likely used, so just commenting out.
		// Initialize encoder.
		Devices.encoder1.reset();
		Devices.encoder2.reset();
		Devices.winchEncoder.reset();
        
		//TODO NavX likely used, so just commenting out.
        // Set gyro/NavX to heading 0.
        //robot.gyro.reset();
		Devices.navx.resetYaw();
		
        // Wait to start motors so gyro will be zero before first movement.
        Timer.delay(.50);

		switch (program) //TODO: Pseudo-code 2 cube autos (maybe 3 block autos?)
		{
			case 0:		// No auto program.
					
					break;
			
			case 1: 	//goo.gl/MKYWUX <= Visualization of auto
					sideBaseline();
					break;
			
			case 2:		//https://goo.gl/RvUmTV <= Visualization of auto
					centerBaseline();
					break;
				
			case 3:		//goo.gl/TBU4oK <= Visualization of auto
					switch (robot.gameMessage.charAt(0))
					{
						case 'L':
							centerSwitchLeft();
							break;
							
						case 'R':
							
							centerSwitchRight();
							break;
							
						default:
							centerBaseline();
							break;
					}
					break;
				
			case 4:		//goo.gl/x5PFJf <= Visualization of auto
				switch (robot.gameMessage.charAt(0))
				{
					case 'L':
						leftSwitch();
						break;
					case 'R':
						if (robot.gameMessage.charAt(1) == 'L')
						{
							centerBaseline();
						} 
						else 
						{
							leftZone();	
						}
						
					default:
						centerBaseline();
						break;
				} 
					
					break;
			
			case 5: 	//goo.gl/MaJi3d <= Visualization of auto
				switch (robot.gameMessage.charAt(0))
				{
					case 'R':
						rightSwitch();
						break;
						
					case 'L':
						if (robot.gameMessage.charAt(1) == 'R')
						{
							centerBaseline();
						}
						else
						{
							rightZone();
						}
					default:
						centerBaseline();
						break;
				}
				
			case 6: //2 cube switch auto if color on left
				switch (robot.gameMessage.charAt(0))
				{
					case 'L':
						twoSwitch();
						break;
						
					case 'R':
						centerBaseline();
						break;
					break;
				}
				break;
		}
		
		Util.consoleLog("end");
	}
	
	private void sideBaseline()
	{
		autoDrive(0.50, 1000, true); //Find actual values for this - Drive forward, crossing the baseline
	}
	
	private void centerBaseline()
	{
		autoDrive(0.50, 500, true); //Find actual values for this - Drive forward
		autoRotate(0.50, 45); //Find actual values for this - Rotate so the robot is facing away from the switch
		autoDrive(0.50, 500, true); //Find actual values for this - Drive forward, away from the switch towards the scale
	}
	
	private void centerSwitchLeft()
	{
		Cube.CubeRaise();
		autoDrive(0.50, 300, true); //Find actual values for this - Move forward
		autoRotate(0.50, 90); //Find actual values for this - Turn to the left
		autoDrive(0.50, 200, true); //Find actual values for this - Move forward, face the switch
		Cube.CubeOuttake();
		autoDrive(0.50, -200, true); //Find actual values for this - Drive backwards, away from the switch
		Cube.CubeLower();
		
	}
	
	private void centerSwitchRight()
	{
		Cube.CubeRaise();
		autoDrive(0.50, 300, true); //Find actual values for this - Move forward
		autoRotate(-0.50, 90); //Find actual values for this - Turn to the right
		autoDrive(0.50, 200, true); //Find actual values for this - Move forward, face the switch
		Cube.CubeOuttake();
		autoDrive(0.50, -200, true); //Find actual values for this - Drive backwards, away from the switch
		Cube.CubeLower();
	}
	
	private void leftSwitch()
	{
		Cube.CubeRaise();
		autoDrive(0.50, 500, true); //Find actual values for this - Move forward
		autoRotate(0.50, 90); //Find actual values for this - Turn to the left
		autoDrive(0.50, 50, true); //Find actual values for this - Move forward a little bit
		Cube.CubeOuttake();
		autoDrive(0.50, -50, true); //Find actual values for this - Drive backwards, away from the switch
		Cube.CubeLower();
	}
	
	private void rightSwitch()
	{
		Cube.CubeRaise();
		autoDrive(0.50, 500, true); //Find actual values for this - Move forward
		autoRotate(-0.50, 90); //Find actual values for this - Turn to the right
		autoDrive(0.50, 50, true); //Find actual values for this - Move forward a little bit
		Cube.CubeOuttake();
		autoDrive(0.50, -50, true); //Find actual values for this - Drive backwards, away from the switch
		Cube.CubeLower();
	}
	
	private void leftScale()
	{
		Cube.CubeRaise(); //TODO: Edit this class so there's some flexibility on how much the cube is raised. Raising the cube to the switch's height won't work on the scale.
		autoDrive(0.50, 800, true); //Find actual values for this - Move forward
		autoRotate(0.50, 90); //Find actual values for this - Turn to the left
		autoDrive(0.50, 50, true); //Find actual values for this - Move forward a little bit
		Cube.CubeOuttake();
		autoDrive(0.50, -50, true); //Find actual values for this - Drive backwards, away from the scale
		Cube.CubeLower();
	}
	
	private void rightScale()
	{
		Cube.CubeRaise(); //TODO: Edit this class so there's some flexibility on how much the cube is raised. Raising the cube to the switch's height won't work on the scale.
		autoDrive(0.50, 800, true); //Find actual values for this - Move forward
		autoRotate(-0.50, 90); //Find actual values for this - Turn to the right
		autoDrive(0.50, 50, true); //Find actual values for this - Move forward a little bit
		Cube.CubeOuttake();
		autoDrive(0.50, -50, true); //Find actual values for this - Drive backwards, away from the scale
		Cube.CubeLower();
	}
	
	private void leftZone()
	{
		Cube.CubeRaise(); //TODO: Edit this class so there's some flexibility on how much the cube is raised. Raising the cube to the switch's height won't work on the scale.
		autoDrive(0.50, 600, true); //Find actual values for this - Move forward
		autoRotate(0.50, 90); //Find actual values for this - Turn to the left
		autoDrive(0.50, 300, true); //Find actual values for this - Move forward
	}
	
	private void rightZone()
	{
		Cube.CubeRaise(); //TODO: Edit this class so there's some flexibility on how much the cube is raised. Raising the cube to the switch's height won't work on the scale.
		autoDrive(0.50, 600, true); //Find actual values for this - Move forward
		autoRotate(-0.50, 90); //Find actual values for this - Turn to the right
		autoDrive(0.50, 300, true); //Find actual values for this - Move forward
	}
	
	private void twoSwitch()
	{
		Cube.CubeRaise(); //TODO: Edit this class so there's some flexibility on how much the cube is raised. Raising the cube to the switch's height won't work on the scale.
		autoDrive(0.70, 600, true); //Find actual values for this - Move forward
		autoRotate(-0.50, 90); //Find actual values for this - Turn to face the switch
		Cube.CubeOuttake(); //Outtake the cube
		Cube.CubeLower();
		autoRotate(-0.50, 50); //Find actual values for this - Turn to get the cube in the zone by the scale
		autoDrive(0.30, 200, true); //Find actual values for this - Move forward a little bit
		Cube.CubeIntake(); //Intake the cube on the side closest to the switch
		autoDrive(-0.30, 200, true); //Find actual values for this - Move backwards
		autoRotate(0.50, 50); //Find actual values for this - Rotate back in front of the scale
		Cube.CubeOuttake(); //Outtake the cube
		Cube.CubeLower();
	}

	//TODO May likely be used, will need modification to work.
	
	// Auto drive in set direction and power for specified encoder count. Stops
	// with or without brakes on CAN bus drive system. Uses gyro/NavX to go straight.
	
	private void autoDrive(double power, int encoderCounts, boolean enableBrakes)
	{
		int		angle;
		double	gain = .03;
		
		Util.consoleLog("pwr=%.2f, count=%d, brakes=%b", power, encoderCounts, enableBrakes);

		if (robot.isComp) Devices.SetCANTalonBrakeMode(enableBrakes);

		Devices.encoder1.reset();
		Devices.navx.resetYaw();
		/*
		while (isAutoActive() && Math.abs(Devices.encoder.get()) < encoderCounts) 
		{
			LCD.printLine(4, "encoder=%d", Devices.encoder.get());
			
			// Angle is negative if robot veering left, positive if veering right when going forward.
			// It is opposite when going backward. Note that for this robot, - power means forward and
			// + power means backward.
			
			//angle = (int) robot.gyro.getAngle();
			angle = (int) Devices.navx.getYaw();

			LCD.printLine(5, "angle=%d", angle);
			
			// Invert angle for backwards.
			
			if (power > 0) angle = -angle;
			
			//Util.consoleLog("angle=%d", angle);
			
			// Note we invert sign on the angle because we want the robot to turn in the opposite
			// direction than it is currently going to correct it. So a + angle says robot is veering
			// right so we set the turn value to - because - is a turn left which corrects our right
			// drift.
			
			//Devices.robotDrive.drive(power, -angle * gain);
			
			Timer.delay(.020);
		}
*/
		//Devices.robotDrive.tankDrive(0, 0, true);				
		
		Util.consoleLog("end: actual count=%d", Math.abs(Devices.encoder1.get()));
	}
	
	// Auto rotate left or right the specified angle. Left/right from robots forward view.
	// Turn right, power is -
	// Turn left, power is +
	// angle of rotation is always +.
	
	private void autoRotate(double power, int angle)
	{
		Util.consoleLog("pwr=%.2f  angle=%d", power, angle);
		
		Devices.navx.resetYaw();
		
		//Devices.robotDrive.tankDrive(power, -power);

		//while (isAutoActive() && Math.abs((int) Devices.navx.getYaw()) < angle) {Timer.delay(.020);} 
		
		//Devices.robotDrive.tankDrive(0, 0);
	}
	
	
	
	
}