package Team4450.Robot11;

import Team4450.Lib.*;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class GearBox {
	
	private Robot robot;
	
	public boolean highGear;
	public boolean lowGear;
	
	public GearBox(Robot robot)
	{
		Util.consoleLog();
		this.robot = robot;
		highGear(); //Low gear by default
	}
	
	public void dispose() {
		Util.consoleLog();
		
	}
	
	private void dashDisplay()
	{
		Util.consoleLog("low=%b, high=%b", highGear, lowGear);
		SmartDashboard.putBoolean("Low Gear", lowGear);
		SmartDashboard.putBoolean("High Gear", highGear);
	}
	
	public void highGear() {
		Util.consoleLog();
		lowGear = false; //Dashboard stuff
		Devices.shiftGears.SetA();
		highGear = true; //Dashboard stuff
		dashDisplay();
	}
	
	
	public void lowGear() {
		Util.consoleLog();
		highGear = false; //Dashboard stuff
		Devices.shiftGears.SetB();
		lowGear = true; //Dashboard stuff
		dashDisplay();
	}
	
	public boolean dashboardHigh() {
		return highGear;
	}
	
	public boolean dashboardLow() {
		return lowGear;
	}

	
	
	
}
