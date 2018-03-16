package Team4450.Robot11;

import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.*;

import Team4450.Lib.NavX;
import Team4450.Lib.Util;
import Team4450.Lib.ValveDA;
import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.PowerDistributionPanel;
import edu.wpi.first.wpilibj.Servo;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.SpeedControllerGroup;
import edu.wpi.first.wpilibj.Talon;
import edu.wpi.first.wpilibj.CounterBase.EncodingType;
import edu.wpi.first.wpilibj.DigitalInput;

public class Devices
{
	  // Motor CAN ID/PWM port assignments (1=left-front, 2=left-rear, 3=right-front, 4=right-rear)
	  private static WPI_TalonSRX	LFCanTalon, LRCanTalon, RFCanTalon, RRCanTalon;
	  private static WPI_TalonSRX	liftMotor, cubeGrabMotor1, cubeGrabMotor2;
	  public static SpeedControllerGroup cubeGrabMotors;
	  
	  //Climber things
	  public final static Talon						climberWinch = new Talon(0);
	  public final static Talon 					partnerWinch = new Talon(1); //Is my understanding of the "liftWinch" correct? Is that the arm?
	  public final static Servo						deployArms = new Servo(2);
	  public final static Servo						balanceServo = new Servo(3);
	  
	  public static DifferentialDrive	robotDrive;

	  public final static Joystick      utilityStick = new Joystick(2);	
	  public final static Joystick      leftStick = new Joystick(0);	
	  public final static Joystick		rightStick = new Joystick(1);	
	  public final static Joystick		launchPad = new Joystick(3);

	  public final static Compressor	compressor = new Compressor(0);	// Compressor class represents the PCM. There are 2.
	  public final static ValveDA		shiftGears = new ValveDA(0);
	  public final static ValveDA		grabValve = new ValveDA(2);
	  public final static ValveDA		wristValve = new ValveDA(4);
	  	
	  
	  public final static AnalogInput				pressureSensor = new AnalogInput(0);
	  
	  public final static PowerDistributionPanel	PDP = new PowerDistributionPanel();

	  public final static DriverStation				ds = DriverStation.getInstance();

	  //NavX
	  public static NavX							navx;
	  
	  //Encoders
	  public final static Encoder 					encoder1 = new Encoder(0, 1, true, EncodingType.k4X);
	  public final static Encoder 					encoder2 = new Encoder(2, 3, true, EncodingType.k4X);
	  public final static Encoder					winchEncoder = new Encoder(4, 5, true, EncodingType.k4X);
	  
	  public static boolean				winchEncoderEnabled = true; //QOL thing
	  public static DigitalInput		winchSwitcher = new DigitalInput(6);
	  
	  
	  // Create RobotDrive object for CAN Talon controllers.
	  
	  
	  public static void InitializeCANTalonDrive()
	  {
		  Util.consoleLog();

		  LFCanTalon = new WPI_TalonSRX(1);
		  LRCanTalon = new WPI_TalonSRX(2);
		  RFCanTalon = new WPI_TalonSRX(3);
		  RRCanTalon = new WPI_TalonSRX(4);
		  cubeGrabMotor1 = new WPI_TalonSRX(5);
		  cubeGrabMotor2 = new WPI_TalonSRX(6);

	      // Initialize CAN Talons and write status to log so we can verify
	      // all the Talons are connected.
	      InitializeCANTalon(LFCanTalon);
	      InitializeCANTalon(LRCanTalon);
	      InitializeCANTalon(RFCanTalon);
	      InitializeCANTalon(RRCanTalon);
	    
	      InitializeCANTalon(cubeGrabMotor1);
	      cubeGrabMotor1.setNeutralMode(NeutralMode.Brake);
	      InitializeCANTalon(cubeGrabMotor2);
	      cubeGrabMotor2.setNeutralMode(NeutralMode.Brake);
	      
	      
	      // Configure CAN Talons with correct inversions.
	      LFCanTalon.setInverted(false);
		  LRCanTalon.setInverted(false);
		  
		  RFCanTalon.setInverted(false);
		  RRCanTalon.setInverted(false);
		  
		  cubeGrabMotor1.setInverted(false);
		  cubeGrabMotor2.setInverted(true);
		  
		  cubeGrabMotors = new SpeedControllerGroup(cubeGrabMotor1, cubeGrabMotor2);
	      
	      // Turn on brake mode for CAN Talons.
	      SetCANTalonBrakeMode(true);
	      
	      // Setup the SpeedControllerGroups for the left and right set of motors.
	      SpeedControllerGroup LeftGroup = new SpeedControllerGroup(LFCanTalon,LRCanTalon);
		  SpeedControllerGroup RightGroup = new SpeedControllerGroup(RFCanTalon,RRCanTalon);
		  
		  robotDrive = new DifferentialDrive(LeftGroup, RightGroup);
	  }

	  // Initialize and Log status indication from CANTalon. If we see an exception
	  // or a talon has low voltage value, it did not get recognized by the RR on start up.
	  
	  public static void InitializeCANTalon(WPI_TalonSRX talon)
	  {
		  //Util.consoleLog("talon init: %s   voltage=%.1f", talon.getDescription(), talon.getBusVoltage());

		  talon.clearStickyFaults(0); //0ms means no blocking.
		  //talon.enableControl();
		  //talon.changeControlMode(ControlMode.PercentOutput); //TODO Find PercentVbus
	  }
	  
	  // Set neutral behavior of CAN Talons. True = brake mode, false = coast mode.

	  public static void SetCANTalonBrakeMode(boolean brakeMode)
	  {
		  Util.consoleLog("brakes on=%b", brakeMode);
		  SmartDashboard.putBoolean("Breaks", brakeMode);
		  
		  NeutralMode newMode;
		  if (brakeMode) {
			  newMode = NeutralMode.Brake;
		  } else {
			  newMode = NeutralMode.Coast;
		  }
		  
		  LFCanTalon.setNeutralMode(newMode);
		  LRCanTalon.setNeutralMode(newMode);
		  RFCanTalon.setNeutralMode(newMode);
		  RRCanTalon.setNeutralMode(newMode);
	  }
	  
	  // Set CAN Talon voltage ramp rate. Rate is volts/sec and can be 2-12v.
	  public static void SetCANTalonRampRate(double seconds)
	  {
		  Util.consoleLog("%f", seconds);
		  
		  LFCanTalon.configOpenloopRamp(seconds, 0);
		  LRCanTalon.configOpenloopRamp(seconds, 0);
		  RFCanTalon.configOpenloopRamp(seconds, 0);
		  RRCanTalon.configOpenloopRamp(seconds, 0);
	  }
	  
	  
	  // Return voltage and current draw for each CAN Talon.
	  public static String GetCANTalonStatus()
	  {
		  return String.format("%.1f/%.1f  %.1f/%.1f  %.1f/%.1f  %.1f/%.1f  %.1f/%.1f  %.1f/%.1f", 
				  LFCanTalon.getMotorOutputVoltage(), LFCanTalon.getOutputCurrent(),
				  LRCanTalon.getMotorOutputVoltage(), LRCanTalon.getOutputCurrent(),
				  RFCanTalon.getMotorOutputVoltage(), RFCanTalon.getOutputCurrent(),
				  RRCanTalon.getMotorOutputVoltage(), RRCanTalon.getOutputCurrent());
				
	  }

}
