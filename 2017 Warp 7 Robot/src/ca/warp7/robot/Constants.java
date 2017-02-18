package ca.warp7.robot;

public class Constants {
	// PWM Pins
	public static final int[] RIGHT_DRIVE_MOTOR_PINS = { 0, 1 };
	public static final int[] LEFT_DRIVE_MOTOR_PINS = { 2, 3 };
	public static final int[] CLIMBER_MOTOR_PINS = { 4, 5};
	public static final int[] HOPPER_SPIN_PINS = { 7 };

	// DIG Pins
	public static final int LEFT_DRIVE_ENCODER_A = 0;
	public static final int LEFT_DRIVE_ENCODER_B = 1;
	public static final int RIGHT_DRIVE_ENCODER_A = 2;
	public static final int RIGHT_DRIVE_ENCODER_B = 3;
	
	// Solenoids (manifold ports)
	public static final int FLIP_PORT = 4;
	public static final int DRIVE_SHIFTER_PORT = 1;
	public static final int GEAR_PISTON_PORT = 0;
	
	// Compressor
	public static final int COMPRESSOR_PIN = 0;

	// CAN ID's
	public static final int SHOOTER_CAN_ID = 0;
	
	// Robot dimensions and stuff
	public static double WHEEL_DIAMETER = 4; // inches
	public static double WHEEL_CIRCUMFERENCE = Math.PI * WHEEL_DIAMETER;
	public static int DRIVE_TICKS_PER_REV = 256;
	public static double DRIVE_INCHES_PER_TICK = WHEEL_CIRCUMFERENCE / DRIVE_TICKS_PER_REV;
	
    // reversed?
    public static boolean BATTERY = false;
    public static boolean INTAKE = true;

    public static String ATTR_EX_MODE = "CameraAttributes::Exposure::Mode";
    public static String ATTR_EX_VALUE = "CameraAttributes::Exposure::Value";

    public static String ATTR_BR_MODE = "CameraAttributes::Brightness::Mode";
    public static String ATTR_BR_VALUE = "CameraAttributes::Brightness::Value";
}
