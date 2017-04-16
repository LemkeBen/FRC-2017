package ca.warp7.robot.auto;

import ca.warp7.robot.Warp7Robot;

public class GearRightRed extends AutonomousBase{

	double rpm = 4706;
	@Override
	public void periodic() {
		Warp7Robot.compressor.setClosedLoopControl(false);
		drive.autoShift(false);
		
		switch(step){
		case 1:
			gearMech.hold();
			if(travel(6*12+1))
				nextStep(0.2);
			break;
		case 2:
			if(relTurn(-55))
				nextStep(0.2);
			break;
		case 3:
			if(travel(1*12))
				nextStep(0.1);
			break;
		case 4:
			drive.autoMove(-0.5, -0.5);
			try{
				if(gearGoalVisible())
					nextStep();
			}catch(Exception e){
				System.err.println("NO JETSON!!!!!!");
				System.err.println("NO JETSON!!!!!!");
				System.err.println("NO JETSON!!!!!!");
				System.err.println("NO JETSON!!!!!!");
				System.err.println("NO JETSON!!!!!!");
			}
			if(timePassed(5)){
				nextStep();
				System.err.println("no target");
				System.err.println("no target");
				System.err.println("no target");
				System.err.println("no target");
				System.err.println("no target");
				endAuto();
			}
			gearMech.hold();
			break;
		case 5:
			try{
				if(gearMove()){
					nextStep(0.2, 2);
					//endAuto();
				}
				
				if(timePassed(4)){
					//gearMech.flippedyFlip();
					nextStep();
					//endAuto();
				}
			}catch(Exception e){
				System.err.println("NO JETSON!!!!!!");
				System.err.println("NO JETSON!!!!!!");
				System.err.println("NO JETSON!!!!!!");
				System.err.println("NO JETSON!!!!!!");
				System.err.println("NO JETSON!!!!!!");
				endAuto();
			}
			break;
		case 6:
			drive.autoMove(-0.4, -0.4);
			if(timePassed(0.75)){
				nextStep(0.2);
			}
			break;
		case 7:
			gearMech.release();
			nextStep(0.5);
			break;
		case 8:
			if(travel(-(2*12), 0.4))
				nextStep(0.5);
			break;
		case 9:
			shooter.setRPM(rpm);
			if(relTurn(15)){
				nextStep(0.5, 2);
			}
			break;
		case 10:
			shooter.setRPM(rpm);
			if(travel(-(1*12+2)))
				nextStep(0.5);
			break;
		case 11:
			shooter.setRPM(rpm);
			try{
				if(lineUpShooter(Direction.COUNTER_CLOCKWISE))
					nextStep(0.5);
			}catch(Exception e){
				System.err.println("NO JETSON!!!!!!");
				System.err.println("NO JETSON!!!!!!");
				System.err.println("NO JETSON!!!!!!");
				System.err.println("NO JETSON!!!!!!");
				System.err.println("NO JETSON!!!!!!");
				endAuto();
			}
			break;
		case 12:
			try{
				if(visionShoot(6))
					nextStep(0.5);
			}catch(Exception e){
				System.err.println("NO JETSON!!!!!!");
				System.err.println("NO JETSON!!!!!!");
				System.err.println("NO JETSON!!!!!!");
				System.err.println("NO JETSON!!!!!!");
				System.err.println("NO JETSON!!!!!!");
				endAuto();
			}
			break;
		default:
			reset();
			break;
		}
	}

}
