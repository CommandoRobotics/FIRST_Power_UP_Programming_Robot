package autonomous.autonomousCommands;

import autonomous.AutonomousCommand;
import edu.wpi.first.wpilibj.Timer;

public class RotateCommand extends AutonomousCommand {
	
	public double angle;
	public RotateCommand(double angle) {
		super(AutonomousCommand.ROTATE);
		this.angle = angle;
	}
}
