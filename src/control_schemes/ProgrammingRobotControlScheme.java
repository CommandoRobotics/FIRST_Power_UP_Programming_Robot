package control_schemes;

import configurations.RobotConfiguration;
import edu.wpi.first.wpilibj.Joystick;

public class ProgrammingRobotControlScheme extends RobotControlScheme {

	public ProgrammingRobotControlScheme(RobotConfiguration robotConfiguration) {
		super(robotConfiguration);
	}

	public void updateXBoxOnly(Joystick xbox) {
		leftJoystick(0, -xbox.getRawAxis(XBOX_LEFT_Y));
		rightJoystick(0, -xbox.getRawAxis(XBOX_RIGHT_Y));
	}

	public void updateJoysticksAndXBox(Joystick leftJoystick, Joystick rightJoystick, Joystick xbox) {
		leftJoystick(leftJoystick.getRawAxis(0), leftJoystick.getRawAxis(1));
		rightJoystick(rightJoystick.getRawAxis(0), rightJoystick.getRawAxis(1));
		gamepadX(xbox.getRawButton(XBOX_LEFT));
		gamepadB(xbox.getRawButton(XBOX_RIGHT));
	}
	
	public void updateRobot() {
		robotConfiguration.updateChassis(left_joystick_x, left_joystick_y, right_joystick_x, right_joystick_y);
		robotConfiguration.togglePneumatics(x || b);
	}
}
