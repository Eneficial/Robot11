//Thise code is pretty much me reusing my "Gear" class from last year.
package Team4450.Robot11;

import Team4450.Lib.*;
import Team4450.Robot11.Devices;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.PIDController;
import edu.wpi.first.wpilibj.Timer;

public class CubeManipulation {

	private Robot robot;
	//private Teleop teleop;
	
	private PIDController PIDController;
	
	public CubeManipulation(Robot robot) 
	{
		Util.consoleLog();
		this.robot = robot;
		CubeStop();
	}

	public void dispose() {
		Util.consoleLog();
		//Dispose stuff
		//if (motor != null) motor.delete();
	}
	
	public void CubeIntake() {
		Util.consoleLog();
		CubeOpen();
		Devices.cubeGrabMotors.set(0.2); 
		//Currently as this is right now, this is meant so the manipulator has to manually stop the intake
		//When a cube is in the system. Not 100% sure if we can do the same thing as last year w/ the gear intake
		
	}
	
	public void CubeOuttake() {
		Util.consoleLog();
		CubeOpen(); //Open wrist to let the cube loose
		Devices.cubeGrabMotors.set(-0.2); //Motors push out cube - Get actual value
		Timer.delay(0.1); //Adjust based on how long it takes to outtake the cube
		Devices.cubeGrabMotors.set(0); //Stop motors
	}
	
	public void CubeStop() {
		Util.consoleLog();
		Devices.cubeGrabMotors.set(0);
		CubeClose();
	}
	
	public void CubeRaise() {
		Util.consoleLog();
		//TODO: Write super cool bug-free code that raises the cube
	}
	
	public void CubeLower() {
		Util.consoleLog();
		//TODO: Write super cool bug-free code that lowers the cube
	}
	
	public void CubeOpen() { //Is done with the wrist
		Util.consoleLog();
		Devices.grabValve.SetB(); //Check this
	}
	
	public void CubeClose() { //Is done with the wrist
		Util.consoleLog();
		Devices.grabValve.SetA(); //Check this
	}
	
}
