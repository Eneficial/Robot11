//README: This is my gearbox code from last year - See if it can be reused with minor changes for this year's robot. 
package Team4450.Robot11;

import Team4450.Lib.*;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class GearBox {
	
	private Robot robot;
	
	public boolean gearLow = false; 
	public boolean gearHigh = false;
	public boolean neutral = false;
	public boolean PTO = false;
	
	//public boolean gearLow = false; 
	//public boolean gearHigh = false;
	//public boolean neutral = false;
	//public boolean PTO = false;
			
	//public ValveDA shift = new ValveDA(0);
	//public ValveDA neutralValve = new ValveDA(4);
	//public ValveDA valve = new ValveDA(2); //PTO 
	
	public GearBox (Robot robot, Teleop teleop)
	{
		Util.consoleLog();
		this.robot = robot;
		//PTOoff();
		//gearLow();
	}
	
	public void dispose()
	{
		Util.consoleLog();
		//if (shift != null) shift.dispose();
		//if (valve != null) valve.dispose();
		//if (neutralValve != null) neutralValve.dispose();
	}
	
	public void dashDisplay() {
		Util.consoleLog();
		SmartDashboard.putBoolean("gearLow", gearLow);
		SmartDashboard.putBoolean("Neutral", neutral);
		SmartDashboard.putBoolean("PTO", PTO);
	}
	
	/*public void gearLow() {
		if (!gearLow) {
			shift.SetA();
			neutralValve.SetA();
		}
		else if (neutral) {
			shift.SetA();
			neutralValve.SetA();
		}
		neutral = false;
		gearLow = true;
		dashDisplay();
	}
	
	
	public void gearHigh() {
		if (gearLow) {
			shift.SetB();
			neutralValve.SetB();
		}
		else if (neutral) { 
			neutralValve.SetA();
			shift.SetB();
			neutralValve.SetB();
		}
		neutral = false;
		gearLow = false;
		dashDisplay();
	}
	
	
	public void neutral() {
		if (!gearLow) {
			shift.SetA();
		}
		else if (gearLow) {
			shift.SetB();
			neutralValve.SetB();
			shift.SetA();
		}
		gearLow = true;
		neutral = true;
		dashDisplay();
		
	}
	
	public void PTOon()
	{
		Util.consoleLog();
		neutral();
		valve.SetA();
		PTO = true;
		dashDisplay();
	}
	
	public void PTOoff()
	{
		Util.consoleLog();
		PTO = false;
		valve.SetB();
		gearLow();
		dashDisplay();
	}
	
	public boolean isPTO()
	{
		return PTO;
	}
	
	public boolean isLowSpeed()
	{
		return gearLow;
	}
	
	public boolean isNeutral()
	{
		return neutral;
	}*/
}
