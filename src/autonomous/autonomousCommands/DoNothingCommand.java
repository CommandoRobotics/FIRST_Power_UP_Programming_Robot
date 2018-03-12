package autonomous.autonomousCommands;

import autonomous.AutonomousCommand;
import edu.wpi.first.wpilibj.Timer;

public class DoNothingCommand extends AutonomousCommand {
	
	public double time;
	public DoNothingCommand(double time) {
		super(AutonomousCommand.DO_NOTHING);
		this.time = time;
	}
}
