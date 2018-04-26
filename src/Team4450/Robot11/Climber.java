package Team4450.Robot11;

import Team4450.Lib.Util;
import Team4450.Lib.LCD;
import Team4450.Robot11.Devices;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Climber {
	
	private Robot robot;
	
	public Climber(Robot robot) 
	{
		Util.consoleLog();
		this.robot = robot;
	}
	
	public void dispose() {
		// TODO Auto-generated method stub
		
	}
	
	public void servoReset() {
		  Util.consoleLog();
		  Devices.barServo.setPosition(0.5);
	  }
	
	public void winchBraker (boolean isBrakeEnabled) {
		Util.consoleLog();
		SmartDashboard.putBoolean("Break", isBrakeEnabled);
		if (!isBrakeEnabled) {
			Devices.winchBreak.SetA();
		} else {
			Devices.winchBreak.SetB();
		}
	
	}

	public void pinActuator(double position) {
		Devices.barServo.setPosition(position);
	}
}
