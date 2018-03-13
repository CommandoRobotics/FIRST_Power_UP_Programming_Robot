package autonomous;

import edu.wpi.first.wpilibj.Timer;

public class AutonomousCommand {
	
	public static final int DO_NOTHING = -1;
	public static final int DRIVE_STRAIGHT = 0;
	public static final int ROTATE = 1;

	public int command;
	
	public AutonomousCommand(int command) {
		this.command = command;
	}
	
	public boolean checkDone(Timer timer) {
		return true;
	}
}
