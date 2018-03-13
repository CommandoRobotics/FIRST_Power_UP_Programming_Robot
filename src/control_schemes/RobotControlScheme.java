package control_schemes;

import configurations.RobotConfiguration;
import edu.wpi.first.wpilibj.Joystick;

public abstract class RobotControlScheme {
	public static final int XBOX_LEFT_Y = 1;
	public static final int XBOX_RIGHT_Y = 5;
	
	public static final int XBOX_TOP = 4;
	public static final int XBOX_BOTTOM = 1;
	public static final int XBOX_LEFT = 3;
	public static final int XBOX_RIGHT = 2;
	public static final int XBOX_LEFT_BUMPER = 5;
	public static final int XBOX_RIGHT_BUMPER = 6;
	
	protected RobotConfiguration robotConfiguration;
	public double left_joystick_x, left_joystick_y, right_joystick_x, right_joystick_y;
	public boolean left_joystick_trigger, right_joystick_trigger, a, b, y, x, left_bumper, right_bumper;
	
	public RobotControlScheme(RobotConfiguration robotConfiguration) {
		this.robotConfiguration = robotConfiguration;
		
		left_joystick_x = left_joystick_y = right_joystick_x = right_joystick_y = 0;
		left_joystick_trigger = right_joystick_trigger = a = b = y = x = left_bumper = right_bumper = false;
	}
	
	public abstract void updateXBoxOnly(Joystick xbox);
	public abstract void updateJoysticksAndXBox(Joystick leftJoystick, Joystick rightJoystick, Joystick xbox);
	
	protected void leftJoystick(double x, double y) {left_joystick_x = x; left_joystick_y = y;}
	protected void rightJoystick(double x, double y)  {right_joystick_x = x; right_joystick_y = y;}
	protected void leftJoystickTrigger(boolean pressed) {left_joystick_trigger = pressed;}
	protected void rightJoystickTrigger(boolean pressed) {right_joystick_trigger = pressed;}
	protected void gamepadA(boolean pressed) {a = pressed;}
	protected void gamepadB(boolean pressed) {b = pressed;}
	protected void gamepadY(boolean pressed) {y = pressed;}
	protected void gamepadX(boolean pressed) {x = pressed;}
	protected void gamepadLeftBumper(boolean pressed) {left_bumper = pressed;}
	protected void gamepadRightBumper(boolean pressed) {right_bumper = pressed;}
	
	protected abstract void updateRobot();
}
