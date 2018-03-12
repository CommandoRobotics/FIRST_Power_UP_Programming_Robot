package APIs;

import edu.wpi.first.wpilibj.Spark;

public class Chassis {
	Spark leftFront;
	Spark rightFront;
	Spark leftRear;
	Spark rightRear;
	
	double scale;
	
	public Chassis(int LFPort, int RFPort, int LRPort, int RRPort) {
		leftFront = new Spark(LFPort);
		rightFront = new Spark(RFPort);
		leftRear = new Spark(LRPort);
		rightRear = new Spark(RRPort);
		
		scale = 0.6;
	}
	
	public void drive(double leftY, double rightY) {
		driveLeftSide(leftY);
		driveRightSide(rightY);
	}
	
	public void driveLeftSide(double leftY) {
		if(Math.abs(leftY) < 0.04) leftY = 0;
		
		leftY *= scale;
		
		leftFront.set(leftY); leftRear.set(leftY);
	}
	
	public void driveRightSide(double rightY) {
		if(Math.abs(rightY) < 0.04) rightY = 0;
		
		rightY *= scale;
		
		rightFront.set(-rightY); rightRear.set(-rightY);
	}
}
