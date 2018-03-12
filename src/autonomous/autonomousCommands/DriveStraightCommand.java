package autonomous.autonomousCommands;

import autonomous.AutonomousCommand;

public class DriveStraightCommand extends AutonomousCommand {
	
	public double time, lockAngle;
	public DriveStraightCommand(double time, double lockAngle) {
		super(AutonomousCommand.DRIVE_STRAIGHT);
		this.time = time;
		this.lockAngle = lockAngle;
	}
}
