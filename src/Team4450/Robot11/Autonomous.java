
package Team4450.Robot11;

import Team4450.Lib.*;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.DriverStation.MatchType;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Autonomous
{
	private final Robot	robot;
	private final int	program = (int) SmartDashboard.getNumber("AutoProgramSelect",0);
	
	private final CubeManipulation Cube;
	private final GearBox gearBox;
	private final Climber climber;
	
	Autonomous(Robot robot)
	{
		Util.consoleLog();
		
		this.robot = robot;		
		Cube = new CubeManipulation(robot); 
		gearBox = new GearBox(robot); 
		climber = new Climber(robot);
	}

	public void dispose()
	{
		Util.consoleLog();
		if (gearBox != null) gearBox.dispose();
		if (Cube != null) { 
			Cube.CubeStop();
			Cube.dispose();
		}
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
		
		//Robot position upon initialization
		climber.servoReset();
		Cube.cubeWristOut();
		Cube.cubeClose();
		climber.winchBraker(false);

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
        
        Devices.SetCANTalonBrakeMode(true);

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
						
					default:
						sideBaseline();
						break;
				} 
					
					break;
			
			case 5: 	//goo.gl/MaJi3d <= Visualization of auto
				switch (robot.gameMessage.charAt(0))
				{
				
					case 'R':
						rightSwitch();
						break;
						
					default:
						sideBaseline();
						break;
				}
				break;
				
			case 6: //Faster Center Auto
				switch (robot.gameMessage.charAt(0))
				{
					case 'L':
						centerSwitchLeftFast();
						break;
					case 'R':
						centerSwitchRightFast();
						break;
					default:
						centerBaseline();
						break;
							

				}
				break;
				
			case 7: //S-Curve
				switch (robot.gameMessage.charAt(0))
				{
					case 'L':
						centerSwitchLeftSCurve();
						break;
					case 'R':
						centerSwitchRightSCurve();
						break;
					default:
						centerBaseline();
						break;

				}
				break;
				
			case 8: //2 cube center
				switch (robot.gameMessage.charAt(0))
				{
					case 'L':
						twoSwitchLeft();
						break;
					case 'R':
						twoSwitchRight();
						break;
					default:
						centerBaseline();
						break;

				}
				break;
		}
		
		Util.consoleLog("end");
	}
	
	private void sideBaseline()
	{
		autoDrive(-0.50, 2490, true);
	}
	
	private void centerBaseline()
	{
		autoDrive(-0.30, 1970, true); 
	}
	
	private void centerSwitchLeft()
	{
		Cube.cubeWristOut();
		Cube.cubeClose();
		Cube.raiseLift(7900);
		autoDrive(-0.50, 924, true); 
		autoRotate(0.50, 90); 
		autoDrive(-0.60, 928, true); //Find actual values for this - Move forward, face the switch
		autoRotate(-0.5, 90);
		autoDrive(-0.4, 880, true);
		Cube.CubeOuttake(0.50);
		Timer.delay(.5);
	}
	
	private void centerSwitchRight()
	{
		Cube.cubeWristOut();
		Cube.cubeClose();
		Cube.raiseLift(7900);
		autoDrive(-0.40, 924, true); 
		autoRotate(-0.50, 90); 
		autoDrive(-0.60, 900, true); 
		autoRotate(0.5, 90);
		autoDrive(-0.4, 880, true);
		Cube.CubeOuttake(0.50);
		Timer.delay(.5);
	}
	
	private void centerSwitchLeftFast() {
		Cube.cubeWristOut();
		Cube.cubeClose();
		Cube.raiseLift(7900);
		autoDrive(-0.40, 100, true);
		autoRotate(0.50, 26);
		autoDrive(-0.50, 2100, true);
		Cube.CubeOuttake(0.5);
		Timer.delay(.5);
	}
	
	private void centerSwitchRightFast() {
		Cube.cubeWristOut();
		Cube.cubeClose();
		Cube.raiseLift(7900);
		autoDrive(-0.40, 100, true);
		autoRotate(-0.50, 19);
		autoDrive(-0.50, 1900, true);
		Cube.CubeOuttake(0.5);
		Timer.delay(.5);
	}
	
	private void centerSwitchLeftSCurve() {
		Cube.cubeWristOut();
		Cube.cubeClose();
		Cube.raiseLift(7900);
		autoSCurve(-.50, 6, 35, 1050);
		Cube.CubeOuttake(0.5);
		Timer.delay(.5);
		autoDrive(0.50, 1400, true);
		Cube.raiseLift(0);
		autoRotate(-0.50, 90);
		autoDrive(-0.50, 850, true);
		autoRotate(0.50, 90);
		Cube.cubeOpen();
		Cube.CubeIntake(0.50);
		autoDrive(-0.60, 350, true);
		Cube.cubeClose();
		autoDrive(0.60, 300, true);
	}
	
	private void centerSwitchRightSCurve() {
		Cube.cubeWristOut();
		Cube.cubeClose();
		Cube.raiseLift(7900);
		autoSCurve(-.50, -6, 30, 950);
		Cube.CubeOuttake(0.5);
		Timer.delay(.5);
		autoDrive(0.50, 1400, true);
		Cube.raiseLift(0);
		autoRotate(0.60, 90);
		autoDrive(-0.60, 850, true);
		autoRotate(-0.60, 90);
		Cube.cubeOpen();
		Cube.CubeIntake(0.5);
		autoDrive(-0.60, 350, true);
		Cube.cubeClose();
		autoDrive(0.60, 300, true);
	}
	
	private void leftSwitch()
	{
		Cube.cubeWristOut();
		Cube.cubeClose();
		Cube.raiseLift(7900);
		autoDrive(-0.50, 3000, true);
		autoRotate(-0.50, 90);
		autoDrive(-0.50, 320, true);
		Cube.CubeOuttake(0.5);
		Timer.delay(.5);
	}
	
	private void rightSwitch()
	{
		Cube.cubeWristOut();
		Cube.cubeClose();
		Cube.raiseLift(7900);
		autoDrive(-0.50, 3000, true); 
		autoRotate(0.50, 90); 
		autoDrive(-0.50, 320, true); 
		Cube.CubeOuttake(0.5);
		Timer.delay(.5);
	}
	
	private void twoSwitchLeft()
	{
		centerSwitchLeftSCurve();
		
	}
	
	private void twoSwitchRight()
	{
		centerSwitchRightSCurve();
		
	}

	//TODO May likely be used, will need modification to work.
	
	// Auto drive in set direction and power for specified encoder count. Stops
	// with or without brakes on CAN bus drive system. Uses gyro/NavX to go straight.
	
	private void autoDrive(double power, int encoderCounts, boolean enableBrakes)
	{
		int		angle;
		double	gain = .05;
		
		Util.consoleLog("pwr=%.2f, count=%d, brakes=%b", power, encoderCounts, enableBrakes);

		if (robot.isComp) Devices.SetCANTalonBrakeMode(enableBrakes);

		Devices.encoder1.reset();
		Devices.encoder2.reset();
		Devices.navx.resetYaw();
		
		if (power > 0) gain = -gain;
		
		
		while (isAutoActive() && Math.abs(Devices.encoder1.get()) < encoderCounts) 
		{
			LCD.printLine(4, "encoder=%d encoder2 = %d", Devices.winchEncoder.get(), Devices.encoder2.get());
			
			// Angle is negative if robot veering left, positive if veering right when going forward.
			// It is opposite when going backward. Note that for this robot, - power means forward and
			// + power means backward.
			
			//angle = (int) robot.gyro.getAngle();
			angle = (int) Devices.navx.getYaw();

			LCD.printLine(5, "angle=%d", angle);
			
			// Invert angle for backwards.
			
			if (power > 0) angle = -angle;
			
			Util.consoleLog("angle=%d", angle);
			
			// Note we invert sign on the angle because we want the robot to turn in the opposite
			// direction than it is currently going to correct it. So a + angle says robot is veering
			// right so we set the turn value to - because - is a turn left which corrects our right
			// drift.
			
			Devices.robotDrive.curvatureDrive(power, angle * gain, false);
			
			Timer.delay(.020);
		}

		Devices.robotDrive.tankDrive(0, 0, true);				
		
		Util.consoleLog("end: actual count=%d actual count2=&d", Math.abs(Devices.encoder1.get()), Math.abs(Devices.encoder2.get()));
	}
	
	// Auto rotate left or right the specified angle. Left/right from robots forward view.
	// Turn right, power is -
	// Turn left, power is +
	// angle of rotation is always +.
	
	private void autoRotate(double power, int angle)
	{
		Util.consoleLog("pwr=%.2f  angle=%d", power, angle);
		
		Devices.navx.resetYaw();
		
		Util.consoleLog("AutoRotate: " + power);
		Devices.robotDrive.tankDrive(power, -power);

		while (isAutoActive() && Math.abs((int) Devices.navx.getYaw()) < angle) {Timer.delay(.020);} 
		
		Util.consoleLog("AutoRotate: Stop. Adjusted Angle Traveled: " + adjustAngle(Devices.navx.getYaw()));
		Devices.robotDrive.tankDrive(0, 0);
	}
	
	private void autoSCurve(double power, double curve, int targetAngle, int straightEncoderCounts)
	{
		double	gain = .05;
		
		Util.consoleLog("pwr=%.2f  curve=%.2f  angle=%d  counts=%d", power, curve, targetAngle, straightEncoderCounts);
		
		// We start out driving in a curve until we have turned the desired angle.
		// Then we drive straight the desired distance then curve back to starting
		// angle. Curve is - for right, + for left.
		
		Devices.robotDrive.curvatureDrive(power, curve * gain, false);
		
		while (isAutoActive() && Math.abs((int) adjustAngle(Devices.navx.getYaw())) < targetAngle) 
		{
			LCD.printLine(6, "angle=%.2f adjusted angle=%.2f", Devices.navx.getYaw(), adjustAngle(Devices.navx.getYaw()));
			Util.consoleLog("angle=%.2f adjusted angle=%.2f", Devices.navx.getYaw(), adjustAngle(Devices.navx.getYaw()));
			Timer.delay(.020);
		}
		
		autoDrive(power, straightEncoderCounts, false);

		Devices.navx.resetYaw();
		
		Devices.robotDrive.curvatureDrive(power*.7, -curve * gain, false);
		
		while (isAutoActive() && Math.abs((int) adjustAngle(Devices.navx.getYaw())) < (targetAngle-10) && ((Devices.ds.getMatchType() != MatchType.None) ? Devices.ds.getMatchTime() > 5 : true)) 
		{
			LCD.printLine(6, "angle=%.2f adjusted angle=%.2f", Devices.navx.getYaw(), adjustAngle(Devices.navx.getYaw()));
			Util.consoleLog("angle=%.2f adjusted angle=%.2f", Devices.navx.getYaw(), adjustAngle(Devices.navx.getYaw()));
			Timer.delay(.020);
		}
		
		Devices.SetCANTalonBrakeMode(true);

		Devices.robotDrive.tankDrive(0, 0, true);
	}
	
	public float adjustAngle(float angle) {
		if (Robot.isClone) return angle*(18.0f/15.0f);
		else return angle;
	}
	
	
}