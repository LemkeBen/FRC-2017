package ca.warp7.robot.auto;

import ca.warp7.robot.Warp7Robot;
import ca.warp7.robot.misc.DataPool;
import ca.warp7.robot.subsystems.Climber;
import ca.warp7.robot.subsystems.Drive;
import ca.warp7.robot.subsystems.GearMech;
import ca.warp7.robot.subsystems.Shooter;
import edu.wpi.first.wpilibj.Timer;

public abstract class AutonomousBase {

	public int step;
	public static DataPool autoPool = new DataPool("auto");
	protected Climber climber;
	protected Drive drive;
	protected GearMech gearMech;
	protected Shooter shooter;
	
	
	public AutonomousBase(){
		climber = Warp7Robot.climber;
		drive = Warp7Robot.drive;
		gearMech = Warp7Robot.gearMech;
		shooter = Warp7Robot.shooter;
		step = 1;
		reset();
	}
	
	public abstract void periodic();

	public void reset(){
		drive.autoMove(0, 0);
		stopShooter();
		gearMech.hold();
		drive.autoShift(true);
		resetValues();
	}
	
	/**
	 * @param degrees
	 *            relative (0 is where you are)
	 *            Positive is to the right
	 */
	private double errorOld = 0.0;
	protected boolean absTurn(double degrees) {
		degrees %= 360;
		if(degrees < 0) degrees += 360;
		double angle = drive.getRotation()%360;
		double kp = 7.0;
		double kd = 3;
		
		double error = degrees-angle;
		double speed = (kp*error/360) + ((error-errorOld)*kd/360);
		if(Math.abs(error) < 3)speed = 0;
		
		
		speed = Math.max(-1, Math.min(1, speed));
		drive.autoMove(speed, -speed);
		errorOld = error;
		return Math.abs(error) < 3;
	}
	
	private boolean resetT = true;
	private double offset = 0.0;
	/**
	 * negative values turn counter clockwise, positive clockwise
	 * 
	 * @param degrees
	 *            relative (0 is where you are)
	 *            Positive is to the right
	 */
	protected boolean relTurn(double degrees) {
		if(resetT)offset = drive.getRotation();

		//degrees -= 10;
		
		double angle = drive.getRotation()-offset;
		double kp = 4.0;
		double kd = 0.0;
		@SuppressWarnings("unused")
		double ki = 0.0;
		
		double error = degrees-angle;
		double speed = (kp*error/360) + ((error-errorOld)*kd/360);
		if(Math.abs(error) < 3)speed = 0;
		
		autoPool.logDouble("gyro error", error);
		autoPool.logBoolean("Turn in Tolerance", Math.abs(error) < 3);
				
		speed = Math.max(-1, Math.min(1, speed));
		drive.autoMove(speed, -speed);
		errorOld = error;
		

		if (Math.abs(error) < 3){
			resetT = true;
			return true;
		}else{
			resetT = false;
			return false;
		}
	}

	private boolean resetT2 = true;
	private double offset2 = 0.0;
	/**
	 * negative values turn counter clockwise, positive clockwise
	 * 
	 * @param degrees
	 *            relative (0 is where you are)
	 *            Positive is to the right
	 */
	protected boolean relTurn(double degrees, double allowableErrorOver, double allowableErrorUnder) {
		if(resetT2)offset2 = drive.getRotation();

		//degrees -= 10;
		
		double angle = drive.getRotation()-offset2;
		double kp = 4.0;
		double kd = 0.0;
		@SuppressWarnings("unused")
		double ki = 0.0;
		
		double error = degrees-angle;
		double speed = (kp*error/360) + ((error-errorOld)*kd/360);
		if(Math.signum(error) == -1 && Math.abs(error) < allowableErrorOver || Math.signum(error) == 1 && Math.abs(error) < allowableErrorUnder)speed = 0;
		
		autoPool.logDouble("gyro error", error);
		autoPool.logBoolean("Turn in Tolerance", Math.signum(error) == -1 && Math.abs(error) < allowableErrorOver || Math.signum(error) == 1 && Math.abs(error) < allowableErrorUnder);
		
		speed = Math.max(-1, Math.min(1, speed));
		drive.autoMove(speed, -speed);
		errorOld = error;
		

		if (Math.signum(error) == -1 && Math.abs(error) < allowableErrorOver || Math.signum(error) == 1 && Math.abs(error) < allowableErrorUnder){
			resetT2 = true;
			return true;
		}else{
			resetT2 = false;
			return false;
		}
	}
	
	private boolean resetD = true;
	private double distance = 0.0;
	private double sumL = 0.0;
	private double sumR = 0.0;
	private int counter = 0;
	private double lStart = 0.0;
	private double rStart = 0.0;
	private double oldErrorL = 0.0;
	private double oldErrorR = 0.0;
	/**
	 * call recursively
	 * 
	 * @param toTravel in inches
	 * @return if it is done
	 */
	protected boolean travel(double toTravel){
		if(resetD){
			lStart = drive.getLeftDistance();
			rStart = drive.getRightDistance();
			distance = toTravel;
		}
		
		/*
		double kp = 2;
		double kd = 0.1;
		double ki = 0.0001;
		*/
		
		double kp = 16;
		double kd = 4;
		double ki = 0;
		
		double errorL = toTravel - (drive.getLeftDistance()-lStart);
		double errorR = toTravel - (drive.getRightDistance()-rStart);
		sumL += errorL;
		sumR += errorR;
		autoPool.logDouble("errorL", errorR);
		autoPool.logDouble("errorR", errorL);
		double speedL = (kp*errorL)/Math.abs(lStart+distance) + ((errorL-oldErrorL)*kd/Math.abs(lStart+distance))+ki*sumL;
		double speedR = (kp*errorR)/Math.abs(rStart+distance) + ((errorR-oldErrorR)*kd/Math.abs(rStart+distance))+ki*sumR;
		if(Math.abs(errorL) < 0.25)speedL = 0;
		if(Math.abs(errorR) < 0.25)speedR = 0;
		
		autoPool.logBoolean("L Encoder in Tolerance", Math.abs(errorR) < 0.25);
		autoPool.logBoolean("R Encoder in Tolerance", Math.abs(errorL) < 0.25);
		
		speedL = Math.max(-0.6,  Math.min(0.6, speedL));
		speedR = Math.max(-0.6, Math.min(0.6, speedR));
		
		drive.autoMove(-speedL, -speedR);
		oldErrorL = errorL;
		oldErrorR = errorR;
		
		if(counter >= 1000){
			sumL = 0.0;
			sumR = 0.0;
		}
		
		if(Math.abs(errorL) < 0.25 && Math.abs(errorR) < 0.25){
			resetD = true;
			return true;
		}else{
			resetD = false;
			return false;
		}
	}
	
	protected boolean visionMove() throws NullPointerException{
		drive.autoMove(DataPool.getDoubleData("vision", "left"), DataPool.getDoubleData("vision", "right"));
		return !DataPool.getBooleanData("vision", "found");
	}
	
	protected boolean gearMove() throws NullPointerException{
		if(visionMove()){
			drive.autoMove(0.3, 0.3);
			Timer.delay(0.5);
			if(!DataPool.getBooleanData("vision", "found")){ // if we don't see the target... finish
				drive.autoMove(0.0, 0.0);
				return true;
			}
		}
		return false;
	}
	
	protected void shoot(double shooterRPM){
		shooter.setRPM(shooterRPM);
		shooter.setHopperSpeed(0.7);
		shooter.setIntakeSpeed(1.0);
		if(shooter.withinRange(40) && shooter.getSetPoint() > 0.0){
			shooter.setTowerSpeed(1.0);
		}else if(shooter.getSensor()){
			shooter.setTowerSpeed(0.4);
		}else{
			shooter.setTowerSpeed(0.0);
		}
	}
	
	protected boolean shoot(double shooterRPM, double seconds){
		shoot(shooterRPM);
		if(timePassed(seconds)){
			stopShooter();
			return true;
		}
		return false;
	}
	
	protected void stopShooter(){
		shooter.setHopperSpeed(0.0);
		shooter.setIntakeSpeed(0.0);
		shooter.setRPM(0.0);
		shooter.setTowerSpeed(0.0);
	}
	
	private double timer = -1;
	protected boolean timePassed(double seconds) {
		if(timer <= 0)
			timer = Timer.getFPGATimestamp();
		
		if(Timer.getFPGATimestamp() - timer >= seconds){
			timer = -1;
			return true;
		}else{
			return false;
		}
	}
	
	protected void nextStep(double delaySeconds){
		step++;
		resetValues();
		drive.autoMove(0, 0);
		Timer.delay(delaySeconds);
	}
	
	private void resetValues(){
		resetD = true;
		resetT = true;
		resetT2 = true;
		offset2 = 0.0;
		timer = -1;
		sumL = 0.0;
		sumR = 0.0;
		counter = 0;
		lStart = 0.0;
		rStart = 0.0;
		oldErrorL = 0.0;
		oldErrorR = 0.0;
		offset = 0.0;
		errorOld = 0.0;
	}
}
