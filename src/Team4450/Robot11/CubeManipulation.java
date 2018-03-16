package Team4450.Robot11;

import Team4450.Lib.*;
import Team4450.Robot11.Devices;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.PIDController;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class CubeManipulation {

	private Robot robot;
	
	private boolean climbWinch = true;
	private boolean holdingPosition;
	private boolean holdingHeight;
	
	private final PIDController PIDController; //For lift
	private Thread IntakeThread;
	
	public CubeManipulation(Robot robot) 
	{
		Util.consoleLog();
		this.robot = robot;
		
		//CubeStop();
		
		//Climber initalization
		PIDController = new PIDController(0.0, 0.0, 0.0, Devices.winchEncoder, Devices.climberWinch);
		Devices.deployArms.set(0);
		Devices.balanceServo.set(1);
		Devices.winchEncoder.reset();
		
		//if (robot.isAutonomous()) {
			//cubeWristIn();
		//}
		//cubeOpen();	
		dashDisplayUpdate();
	}

	public void dispose() {
		Util.consoleLog();
		PIDController.disable();
		PIDController.free();
		if (IntakeThread != null) {
			autoIntakeStop();
		}
	}
	
	private void dashDisplayUpdate() {
		//Stuff in here for later
	}
	
	public void cubeOpen() {
		Util.consoleLog();
		//SmartDashboard.putBoolean("Grabber", grabberOpen);
		Devices.grabValve.SetB(); //Is it supposed to be A?
	}
	
	public void cubeClose() {
		Util.consoleLog();
		Devices.grabValve.SetA(); //Is it supposed to be B?
		dashDisplayUpdate();
	}
	
	public void cubeWristIn() { //Flips cube intake in
		Util.consoleLog();
		//Insert boolean here
		Devices.wristValve.SetA();
		dashDisplayUpdate();
	}
	
	public void cubeWristOut() { //Flips cube intake out
		Util.consoleLog();
		//Insert boolean here
		Devices.wristValve.SetB();
		dashDisplayUpdate();
		
	}
	
	public void CubeIntake(double power) { //Uses motors to bring the cubes in
		Util.consoleLog("%0.2f", power);
		//Boolean
		//Boolean
		Devices.cubeGrabMotors.set(power); 
		dashDisplayUpdate();
	}
	
	public void CubeOuttake(double power) { //Uses motors to bring cubes out
		//Util.consoleLog("%0.2f", power);
		//Boolean
		//Boolean
		Devices.cubeGrabMotors.set(-power); //Motors push out cube - Get actual value
		dashDisplayUpdate();
	}
	
	public void CubeStop() {
		Util.consoleLog();
		//Boolean
		//Boolean
		Devices.cubeGrabMotors.set(0);
		cubeClose();
	}
	
	public boolean isClimbWinchSelected()
	{
		Util.consoleLog();
		
		return climbWinch;
	}
	
	
	public boolean isHoldingHeight()
	{
		return holdingHeight;
	}
	
	public boolean isHoldingPosition()
	{
		return holdingPosition;
	}
	
	//******** Lift Mechanisms *************//
	
		public void powerControl(double power) {
			if (climbWinch) {
				if (robot.isClone) {
					power = power * -1;
				}
				
				if (Devices.winchEncoderEnabled) {
					if (robot.isComp) {
						if ((power > 0 && Devices.winchEncoder.get() < 10800) || (power < 0 && !Devices.winchSwitcher.get())) {
							Devices.climberWinch.set(power);
						} else {
							if (Devices.winchSwitcher.get()) {
								Devices.winchEncoder.reset();
							}
							
							Devices.climberWinch.set(0);
						}
					} else {
						if ((power > 0 && Devices.winchEncoder.get() < 10800) || (power < 0 && Devices.winchSwitcher.get())) {
							Devices.climberWinch.set(power);
						} else {
							if (!Devices.winchSwitcher.get()) {
								Devices.winchEncoder.reset();
								Devices.climberWinch.set(0);
							}
						}
					}
				}
			} else {
				Devices.climberWinch.set(power);
			}
		}
		
		public void raiseLift(int PIDCount) {
			Util.consoleLog("%d", PIDCount);
			if (PIDCount != 0)
			{
				if (isHoldingPosition()) 
					holdLift(0);
				
				
				PIDController.setPID(0.0003, 0.0001, 0.0, 0.50);
				PIDController.setOutputRange(-1, 1);
				PIDController.setSetpoint(PIDCount);
				PIDController.setPercentTolerance(5);
				PIDController.enable();
				//Boolean
			} else {
				PIDController.disable();
				//Boolean
			}
		
		}
		
		public void holdLift(double speed) {
			Util.consoleLog("%f", speed);
			if (speed != 0)
			{
				if (isHoldingHeight()) {
					raiseLift(-1);
				}
				PIDController.setPID(0.0003, 0.0001, 0.0, speed);
				PIDController.setSetpoint(Devices.winchEncoder.get());
				PIDController.setPercentTolerance(5);
				PIDController.enable();
				//Boolean
			} else {
				PIDController.disable();
				//Boolean
			}
		}
		
		
		
		
	
	//******** Cube Intake Thread *************//
	
	public void autoIntakeStart() {
		Util.consoleLog();
		if (IntakeThread != null)
			return;
		IntakeThread = new Thread();
		IntakeThread.start();
	}
	
	public void autoIntakeStop() {
		Util.consoleLog();
		if (IntakeThread != null) {
			IntakeThread.interrupt();
			IntakeThread = null;
		}
		
	}
	
	private class IntakeThread extends Thread {
		IntakeThread() {
			Util.consoleLog();
			this.setName("AutoIntakeThread");
		}
		
		public void threadRun() {
			Util.consoleLog();
			double stopCurrent;
			if (robot.isClone) {
				stopCurrent = 20.0;
			} else {
				stopCurrent = 15.0;
		
			try {
				//autoIntake = true;
				dashDisplayUpdate();
				CubeIntake(0.50);
				sleep(250);
				
				if(!interrupted()) 
					Util.consoleLog("ERROR 404: Cube missing from intake system. Jk jk, there's actually a cube there.");
					sleep(500);
			}
				
				
				catch (InterruptedException e) {CubeStop();}
				catch (Throwable e) {e.printStackTrace(Util.logPrintStream);}
				finally {CubeStop();}
				
				//autoIntake = false;
				//autoIntakeThread = null;
				dashDisplayUpdate();
			
			}
	}
		
		
	
	
	
	}
}
