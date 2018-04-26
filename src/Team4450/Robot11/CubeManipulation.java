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
	private boolean openClose, intake, autoIntake, outtake, deployedNot; 
	
	private final PIDController PIDController; //For lift
	private Thread IntakeThread;
	
	public CubeManipulation(Robot robot) 
	{
		Util.consoleLog();
		this.robot = robot;
		
		//Climber initalization
		PIDController = new PIDController(0.0003, 0.00001, 0.0003, 0, Devices.winchEncoder, Devices.climberWinch);
		Devices.winchEncoder.reset();

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
		SmartDashboard.putBoolean("Grabber", openClose);
		SmartDashboard.putBoolean("Deployed", deployedNot);
		SmartDashboard.putBoolean("Intake", intake);
		SmartDashboard.putBoolean("Spit", outtake);
		SmartDashboard.putBoolean("AutoGrab", autoIntake);
	}
	
	public void cubeOpen() {
		Util.consoleLog();
		openClose = true;
		Devices.grabValve.SetB(); //Is it supposed to be A?
		dashDisplayUpdate();
	}
	
	public void cubeClose() {
		Util.consoleLog();
		openClose = false;
		Devices.grabValve.SetA(); //Is it supposed to be B?
		dashDisplayUpdate();
	}
	
	public void cubeWristIn() { //Flips cube intake in
		Util.consoleLog();
		deployedNot = false;
		Devices.wristValve.SetB();
		dashDisplayUpdate();
	}
	
	public void cubeWristOut() { //Flips cube intake out
		Util.consoleLog();
		deployedNot = true;
		Devices.wristValve.SetA();
		dashDisplayUpdate();
		
	}
	
	public void CubeIntake(double power) { //Uses motors to bring the cubes in
		//Util.consoleLog("%0.2f", power);
		intake = true;
		outtake = false;
		Devices.cubeGrabMotors.set(power); 
		dashDisplayUpdate();
	}
	
	public void CubeOuttake(double power) { //Uses motors to bring cubes out
		Util.consoleLog();
		intake = false;
		outtake = true;
		Devices.cubeGrabMotors.set(-power); //Motors push out cube - Get actual value
		dashDisplayUpdate();
	}
	
	public void CubeStop() {
		Util.consoleLog();
		intake = false;
		outtake = false;
		Devices.cubeGrabMotors.set(0);
		dashDisplayUpdate();
	}

	
	//******** Lift Mechanisms *************//
	
	
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
	
	public void winchSelect() {
		Util.consoleLog();
		powerControl(0);
	}
	
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
						if ((power > 0 && Devices.winchEncoder.get() < 14000) || (power < 0 && Devices.winchSwitcher.get())) {
							Devices.climberWinch.set(power);
						} else {
							if (Devices.winchSwitcher.get()) {
								Devices.winchEncoder.reset();
							}
							Devices.climberWinch.set(0);
						}
					}
				} else {
					Devices.climberWinch.set(power);
				}
			} else {
				Devices.partnerWinch.set(power);
			} 	
		}
		
		public void raiseLift(int PIDCount) {
			Util.consoleLog("%d", PIDCount);
			if (PIDCount >= 0)
			{
				if (isHoldingPosition()) 
					holdLift(0);
				
				if (PIDController.isEnabled())
					PIDController.disable();
				
				PIDController.setPID(0.0003, 0.0001, 0.0003, 0.0);
				PIDController.setOutputRange(-1, 1);
				PIDController.setSetpoint(PIDCount);
				PIDController.setPercentTolerance(1);
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
				PIDController.setPID(0.0003, 0.0001, 0.0003, speed);
				PIDController.setSetpoint(Devices.winchEncoder.get());
				PIDController.setPercentTolerance(1);
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
		Util.consoleLog();
		IntakeThread = new IntakeThread();
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
		
		public void run() {
			Util.consoleLog();
			double stopCurrent;
			if (robot.isClone) {
				stopCurrent = 20.0;
			} else {
				stopCurrent = 15.0;
		
			try {
				autoIntake = true;
				dashDisplayUpdate();
				CubeIntake(0.50);
				sleep(250);
				while (!isInterrupted() && robot.isEnabled() && Devices.cubeGrabMotor1.getOutputCurrent() < stopCurrent) {
					Timer.delay(0.02);
				}
				
				if(!interrupted()) 
					Util.consoleLog("ERROR 404: Cube not found.");
			}
				
				
				catch (InterruptedException e) {CubeStop();}
				catch (Throwable e) {e.printStackTrace(Util.logPrintStream);}
				finally {CubeStop();}
				
				autoIntake = false;
				IntakeThread = null;
				dashDisplayUpdate();
			
			}
	}
		
		
	
	
	
	}
}
