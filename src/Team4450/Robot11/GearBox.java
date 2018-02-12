//README: This is my gearbox code from last year - See if it can be reused with minor changes for this year's robot. 
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
		lowGear(); //Low gear by default
	}
	
	public void dispose() {
		// TODO Auto-generated method stub
		
	}
	
	private void dashDisplay()
	{
		Util.consoleLog("low=%b, high=%b", highGear, lowGear);
		SmartDashboard.putBoolean("Low Gear", lowGear);
		SmartDashboard.putBoolean("High Gear", highGear);
	}
	
	public void highGear() {
		Util.consoleLog();
		highGear = true;
		Devices.shiftGears.SetB();
		dashDisplay();
	}
	
	
	public void lowGear() {
		Util.consoleLog();
		lowGear = true;
		Devices.shiftGears.SetA();
		dashDisplay();
	}

	
	
	
}
