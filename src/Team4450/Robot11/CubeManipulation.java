//Thise code is pretty much me reusing my "Gear" class from last year.
package Team4450.Robot11;

import Team4450.Lib.*;
import Team4450.Robot11.Devices;
import edu.wpi.first.wpilibj.Encoder;
import com.ctre.CANTalon;

public class CubeManipulation {

	private Robot robot;
	private Teleop teleop;
	
	private CANTalon motor = new CANTalon(7); //TODO: Figure out what's different with CANTalon stuff, and why this is being weird.
	
	public Cube(Robot, robot, Teleop, teleop) //Why is this erroring?
	{
		Util.consoleLog();
		this.robot = robot;
		this.teleop = teleop;
	}

	public void dispose() {
		Util.consoleLog();
		//Dispose stuff
		if (motor != null) motor.delete(); //How do I do this again?
	}
	
	public void CubeIntake() {
		Util.consoleLog();
		motor.set(50); //TODO: Find the actual value
		
	}
	
	public void CubeOuttake() {
		Util.consoleLog();
		motor.set(-50); //TODO: Find the actual value
	}
	
	public void CubeStop() {
		Util.consoleLog();
		motor.set(0);
	}
	
	public void CubeRaise() {
		Util.consoleLog();
		//Super cool code that raises the cube
	}
	
	public void CubeLower() {
		Util.consoleLog();
		//More super cool code that lowers the cube
	}
}

