//Thise code is pretty much me reusing my "Gear" class from last year.
package Team4450.Robot11;

import Team4450.Lib.*;
import Team4450.Robot11.Devices;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.PIDController;
import edu.wpi.first.wpilibj.Timer;

public class CubeManipulation {

	private Robot robot;

	private PIDController PIDController;
	
	public CubeManipulation(Robot robot) 
	{
		Util.consoleLog();
		this.robot = robot;
		CubeStop();
		PIDController = new PIDController(0.0, 0.0, 0.0, Devices.winchEncoder, null);
	}

	public void dispose() {
		Util.consoleLog();
		PIDController.disable();
		PIDController.free();
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
	
	public void CubeOpen() { //Is done with the wrist
		Util.consoleLog();
		Devices.grabValve.SetB(); //Check this
	}
	
	public void CubeClose() { //Is done with the wrist
		Util.consoleLog();
		Devices.grabValve.SetA(); //Check this
	}
	
	public void CubeRaise(int PIDCount) {
		Util.consoleLog();
		if (PIDCount != 0)
		{
			PIDController.setPID(0.1, 0.1, 0.1, 1);
			PIDController.setSetpoint(PIDCount);
			PIDController.enable();
		}
	
	}
	
	public void CubeHold(int PIDCount) {
		Util.consoleLog();
		if (PIDCount != 0)
		{
			PIDController.setPID(0.1, 0.1, 0.1, 1);
			PIDController.setSetpoint(Devices.winchEncoder.get());
			PIDController.enable();
		}
	}
	
	
	
}
