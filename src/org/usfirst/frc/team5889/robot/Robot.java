package org.usfirst.frc.team5889.robot;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.kauailabs.navx.frc.AHRS;

import APIs.Chassis;
import APIs.PneumaticsModule;
import autonomous.AutonomousCommand;
import autonomous.autonomousCommands.DoNothingCommand;
import autonomous.autonomousCommands.DriveStraightCommand;
import autonomous.autonomousCommands.RotateCommand;
import autonomous_recorder.AutonomousRecorder;
import configurations.ProgrammingRobotConfiguration;
import control_schemes.ProgrammingRobotControlScheme;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.SPI;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 */
public class Robot extends IterativeRobot {
	
	//Robot Positions
	private static final int LEFT = 1;
	private static final int CENTER = 2;
	private static final int RIGHT = 3;
		
	//Controls
	Joystick leftJoystick, rightJoystick;
	Joystick xbox;
	
	Chassis chassis;
	PneumaticsModule pneumaticsModule;
	AHRS ahrsGyro;
	
	//NAV-X Gyroscope
	private static final double DRIVE_STRAIGHT_PROPORTIONAL_CORRECTION = 0.025;
	private static final double ROTATION_PROPORTIONAL_CORRECTION = 0.0025;
	
	//Autonomous Constants
	private static final double DRIVE_STRAIGHT_BASE_POWER = 0.6;
	private static final double ROTATE_BASE_POWER = 0.3;
	
	private static final double START_TO_SWITCH_TIME = 3;
	private static final double START_TO_SCALE_TIME = 7;
	private static final double SWITCH_TO_SCALE_TIME = 4;
	private static final double SWITCH_SIDES_TIME = 6;
	private static final double WALL_TO_SWITCH_MIDWAY_TIME = 1.2;
	
	//Autonomous Objectives
	private int currentObjective;
	private static final int DELAY = 0;
	private static final int SCORE_ON_SWITCH = 1;
	private static final int SCORE_ON_SCALE = 2;
	private static final int PICK_UP_POWER_CUBE = 3;
	private static final int CROSS_AUTO_LINE = 4;
	private static final int STOP = 5;
	
	//Autonomous Locations
	private static final int LEFT_START = 1; 
	private static final int CENTER_START = 2;
	private static final int RIGHT_START = 3;
	private static final int LEFT_SWITCH_SIDE_MIDWAY = 4;
	private static final int RIGHT_SWITCH_SIDE_MIDWAY = 5;
	private static final int LEFT_SWITCH_SIDE = 6;
	private static final int RIGHT_SWITCH_SIDE = 7;
	private static final int BLOCK_1 = 8; //Leftmost
	private static final int BLOCK_2 = 9;
	private static final int BLOCK_3 = 10;
	private static final int BLOCK_4 = 11;
	private static final int BLOCK_5 = 12;
	private static final int BLOCK_6 = 13; //Rightmost
	private static final int SCALE_SWITCH_MIDWAY = 14; 
	private static final int RIGHT_SWITCH_SCALE_MIDWAY = 15;
	private static final int LEFT_SCALE_SIDE = 16;
	private static final int RIGHT_SCALE_SIDE = 17;
	
	//Robot Mechanisms and Controls
	ProgrammingRobotConfiguration robotConfiguration;
	ProgrammingRobotControlScheme robotControlScheme;
	
	//Autonomous Selection
	SmartDashboard dashboard;
	SendableChooser<String> autonomousChooser;
	SendableChooser<String> robotPositionChooser;
	SendableChooser<String> crossOverIfNecessary;
	
	AutonomousRecorder autonomousRecorder;
	
	//Autonomous Data
	String gameData = "";
	int location = -1;
	boolean switchSides = false;
	
	private List<AutonomousCommand> autonomousCommands;
	private AutonomousCommand currentCommand;
	
	private boolean autonomousProgramComplete = false;
	private int autonomousCommandPoint = 0;
	
	Timer timer = new Timer();
		boolean timerStarted = false;

	//Joystick variables
	double xValue;
	double yValue;
	
	// BARBER: Delete dead code. It will make things quicker to debug.
	/*
	
	public void robotInit() {	
		
	}

	public void autonomousInit() {
		hybridWheels.close();
	}
	 
	 */
		
	public void robotInit() {
		chassis = new Chassis(0, 1, 2, 3);
		// BARBER: Dead code
//		pneumaticsModule = new PneumaticsModule(0, 1);
		
		robotConfiguration = new ProgrammingRobotConfiguration(chassis, pneumaticsModule);
		robotControlScheme = new ProgrammingRobotControlScheme(robotConfiguration);
		
		// BARBER: Dead code
//		autonomousRecorder = new AutonomousRecorder();
		
		try {
			ahrsGyro = new AHRS(SPI.Port.kMXP);
		} catch(RuntimeException e) {
			DriverStation.reportError("Nav-x failed to instantiate: " + e.getMessage(), true);
		}
		
		leftJoystick = new Joystick(0);
		rightJoystick = new Joystick(1);
		xbox = new Joystick(2);	
		
		autonomousCommands = new ArrayList<>();
		
		autonomousChooser = new SendableChooser<>();
		autonomousChooser.addDefault("Don't Run Program", "Don't Run Program");
		autonomousChooser.addObject("Rotate Ninety Degrees", "Rotate Ninety Degrees");
		autonomousChooser.addObject("Drive and Turn", "Drive and Turn");
		autonomousChooser.addObject("Drive To Switch And Drop", "Drive To Switch And Drop");
		autonomousChooser.addObject("Drive To Scale And Drop", "Drive To Scale And Drop");
		
		robotPositionChooser = new SendableChooser<>();
		robotPositionChooser.addDefault("Left", "Left");
		robotPositionChooser.addObject("Center", "Center");
		robotPositionChooser.addObject("Right", "Right");
		
		crossOverIfNecessary = new SendableChooser<>();
		crossOverIfNecessary.addDefault("Do Not Switch Sides For Any Reason", "Do Not Switch Sides For Any Reason");
		crossOverIfNecessary.addObject("Switch Sides To Reach Correct Color", "Switch Sides To Reach Correct Color");
		
		SmartDashboard.putData("Autonomous Selection", autonomousChooser);
		SmartDashboard.putData("Robot Position Chooser", robotPositionChooser);
		SmartDashboard.putData("Cross Over If Necessary", crossOverIfNecessary);
	}

	public void autonomousInit() {
		System.out.println("Autonomous Init Called.");
		
		gameData = DriverStation.getInstance().getGameSpecificMessage();
		location = deriveRobotLocation();
		switchSides = deriveSwitchSides();
		
		resetGyro();
		
		autonomousCommands.clear();
		autonomousCommandPoint = 0;
		autonomousProgramComplete = false;
		
		if(autonomousChooser.getSelected().equals("Cross Auto Line")) crossAutoLine(location, switchSides);
		else if(autonomousChooser.getSelected().equals("Drive To Switch And Drop")) driveToSwitchAndDrop(location, deriveScoringPosition(gameData.charAt(0)), switchSides);
		else if(autonomousChooser.getSelected().equals("Drive To Scale And Drop")) driveToScaleAndDrop(location, deriveScoringPosition(gameData.charAt(1)), switchSides);
		else if(autonomousChooser.getSelected().equals("Cycle Through Objectives"))
			cycleThroughObjectives(location, deriveScoringPosition(gameData.charAt(0)), deriveScoringPosition(gameData.charAt(1)), switchSides, 0);
		
		if(autonomousCommands.size() > 0) currentCommand = autonomousCommands.get(autonomousCommandPoint);
		else autonomousProgramComplete = true;
		
		System.out.println("autonomousCommands.size() = " + autonomousCommands.size());
	}

	private void crossAutoLine(int robotLocation, boolean switchSides) {
		driveWithGyroTime(4);
	}
	
	public void resetGyro() {
		ahrsGyro.reset();
	}
	
	private void driveWithGyroTime(double seconds) {
		double angleToLock = ahrsGyro.getAngle();
		
		timer.reset();
		timer.start();
		
		double basePower = 0.6;
		while(timer.get() < seconds && DriverStation.getInstance().isEnabled()) {
			double error = ahrsGyro.getAngle() - angleToLock;
			double offset = DRIVE_STRAIGHT_PROPORTIONAL_CORRECTION * error;
			if (offset > .25) { offset = .25; }
			chassis.drive(basePower - offset, basePower + offset);
			System.out.println("Drive Straight: " + ahrsGyro.getAngle() + " ---> " + angleToLock);
		}
		timer.stop();
		timer.reset();
		
		chassis.drive(0, 0);
	}
	
	private void rotate(double rotation) {
		double basePower = 0.3;
		while(Math.abs(rotation - ahrsGyro.getAngle()) > 3 && DriverStation.getInstance().isEnabled()) {
			System.out.println("Rotate: " + ahrsGyro.getAngle() + " --> " + rotation);
			
			double error = rotation - ahrsGyro.getAngle();
			double offset = ROTATION_PROPORTIONAL_CORRECTION * error;
			if(offset > 0.6) offset = 0.6;
			
			double totalPower = basePower + Math.abs(offset);
			
			if(error > 0) chassis.drive(totalPower, -totalPower); //Turn Right
			if(error < 0) chassis.drive(-totalPower, totalPower); //Turn Right
			
			//Check and make sure.
			if(Math.abs(rotation - ahrsGyro.getAngle()) < 3) {
				chassis.drive(0, 0);
				timer.reset();
				timer.start();
				Timer.delay(0.2);
			}
		}
		chassis.drive(0, 0);
	}
	
	private void routeToSwitch(int startingLocation, int currentLocation, int scoringPosition, boolean switchSides) {
		if(currentLocation == LEFT_START && startingLocation == scoringPosition) {
			autonomousCommands.add(new RotateCommand(0));
			autonomousCommands.add(new DriveStraightCommand(START_TO_SWITCH_TIME, 0));
			autonomousCommands.add(new RotateCommand(90));
			autonomousCommands.add(new DriveStraightCommand(1, 90));
		} else if(currentLocation == CENTER_START && startingLocation == scoringPosition) {
			autonomousCommands.add(new RotateCommand(0));
			autonomousCommands.add(new DriveStraightCommand(WALL_TO_SWITCH_MIDWAY_TIME, 0));
			int rotation = (scoringPosition == LEFT) ? -90 : 90;
			autonomousCommands.add(new RotateCommand(rotation));
			autonomousCommands.add(new DriveStraightCommand(SWITCH_SIDES_TIME / 2, rotation));
		} else if(currentLocation == RIGHT_START && startingLocation == scoringPosition) {
			autonomousCommands.add(new RotateCommand(0));
			autonomousCommands.add(new DriveStraightCommand(START_TO_SWITCH_TIME, 0));
			autonomousCommands.add(new RotateCommand(-90));
			autonomousCommands.add(new DriveStraightCommand(1, -90));
		} else if(currentLocation == LEFT_SCALE_SIDE && startingLocation == scoringPosition) {
			autonomousCommands.add(new RotateCommand(180));
			autonomousCommands.add(new DriveStraightCommand(SWITCH_TO_SCALE_TIME, 0));
			autonomousCommands.add(new RotateCommand(90));
			autonomousCommands.add(new DriveStraightCommand(1, 90));
		} else if(currentLocation == RIGHT_SCALE_SIDE && startingLocation == scoringPosition) {
			autonomousCommands.add(new RotateCommand(180));
			autonomousCommands.add(new DriveStraightCommand(SWITCH_TO_SCALE_TIME, 0));
			autonomousCommands.add(new RotateCommand(-90));
			autonomousCommands.add(new DriveStraightCommand(1, -90));
		}
	}
	
	private void driveToSwitchAndDrop(int location, int scoringPosition, boolean switchSides) {
//		if(subState == 0) {
//			if(!timerStarted) {
//				timer.reset();
//				timer.start();
//				timerStarted = true;
//			}
//			
//			if(timer.get() > startToSwitchInitialDelay) {
//				subState = 1;
//			}
//		}
		
		
		
		if(scoringPosition == location) {
			double rotation = (location == LEFT) ? 90 : -90;
			
//			driveWithGyroTime(3); //Drive off of the wall and next to the switch
//			rotate(rotation); //Turn to face the switch
//			driveWithGyroTime(1); //Run into the switch
			
			autonomousCommands.add(new DriveStraightCommand(3, 0));
			autonomousCommands.add(new RotateCommand(rotation));
			autonomousCommands.add(new DriveStraightCommand(1, rotation));
		} else if(location == CENTER) {
			double rotation = (scoringPosition == LEFT) ? -90 : 90;
			
//			driveWithGyroTime(2); //Drive off of the wall
//			rotate(rotation); //Turn towards the scoring location
//			driveWithGyroTime(2); //Drive to that side of the switch
//			rotate(0); //Turn back to face forward
//			driveWithGyroTime(1.2); //Drive next to the switch
//			rotate(-rotation); //Turn to face the switch
//			driveWithGyroTime(1); //Run into the switch
			
			autonomousCommands.add(new DriveStraightCommand(2, 0));
			autonomousCommands.add(new RotateCommand(rotation));
			autonomousCommands.add(new DriveStraightCommand(2, rotation));
			autonomousCommands.add(new RotateCommand(0));
			autonomousCommands.add(new DriveStraightCommand(1.2, 0));
			autonomousCommands.add(new RotateCommand(-rotation));
			autonomousCommands.add(new DriveStraightCommand(1, -rotation));
		}
	}
	
	private void driveToScaleAndDrop(int location, int scoringPosition, boolean switchSides) {
		Timer.delay(1);
		if(scoringPosition == location) {
			double rotation = (location == LEFT) ? 90 : -90;
			
			driveWithGyroTime(6); //Drive off of the wall and next to the switch
			rotate(rotation); //Turn to face the switch
			driveWithGyroTime(1); //Run into the switch
		} else if(location == CENTER) {
			double rotation = (scoringPosition == LEFT) ? -90 : 90;
			
			driveWithGyroTime(2); //Drive off of the wall
			rotate(rotation); //Turn towards the scoring location
			driveWithGyroTime(2); //Drive to that side of the switch
			rotate(0); //Turn back to face forward
			driveWithGyroTime(4.2); //Drive next to the switch
			rotate(-rotation); //Turn to face the switch
			driveWithGyroTime(1); //Run into the switch
		}
	}
	
	public void cycleThroughObjectives(int location, int switchScoringPosition, int scaleScoringPosition, boolean switchSides, double delay) {
		//Delay
		Timer.delay(delay);
		
	}
	
	public void autonomousPeriodic() {
		if(autonomousProgramComplete) return;
		
		if(currentCommand.command == AutonomousCommand.DO_NOTHING) {
			if(autonomousCheckDoNothing((DoNothingCommand) currentCommand, timer)) advanceCommand();
		} else if(currentCommand.command == AutonomousCommand.DRIVE_STRAIGHT) {
			if(autonomousCheckDriveStraight((DriveStraightCommand) currentCommand, timer)) advanceCommand();
		} else if(currentCommand.command == AutonomousCommand.ROTATE) {
			if(autonomousCheckRotate((RotateCommand) currentCommand)) advanceCommand();
		}
	}
	
	private void advanceCommand() {
		endCommand(currentCommand.command);
		
		autonomousCommandPoint++;
		if(autonomousCommandPoint > autonomousCommands.size() - 1) autonomousProgramComplete = true;
		else currentCommand = autonomousCommands.get(autonomousCommandPoint);
	}
	
	private boolean autonomousCheckDoNothing(DoNothingCommand command, Timer timer) {
		if(timer.get() < 0.001) timer.start();
		return (timer.get() > command.time);
	}
	
	private boolean autonomousCheckDriveStraight(DriveStraightCommand command, Timer timer) {
		if(timer.get() < 0.001) timer.start();
		double angleToLockOnto = command.lockAngle;
		double error = ahrsGyro.getAngle() - angleToLockOnto;
		double offset = DRIVE_STRAIGHT_PROPORTIONAL_CORRECTION * error;
		if (offset > .25) { offset = .25; }
		chassis.drive(DRIVE_STRAIGHT_BASE_POWER - offset, DRIVE_STRAIGHT_BASE_POWER + offset);
		System.out.println(timer.get() + " > " + command.time);
		return (timer.get() > command.time);
	}
	
	private boolean autonomousCheckRotate(RotateCommand command) {
		if(timer.get() < 0.001) timer.start();
		System.out.println("Rotate: " + ahrsGyro.getAngle() + " --> " + command.angle);
		
		double error = command.angle - ahrsGyro.getAngle();
		double offset = ROTATION_PROPORTIONAL_CORRECTION * error;
		if(offset > 0.6) offset = 0.6;
		
		double totalPower = ROTATE_BASE_POWER + Math.abs(offset);
		
		if(error > 0) chassis.drive(totalPower, -totalPower); //Turn Right
		if(error < 0) chassis.drive(-totalPower, totalPower); //Turn Right
		
		//Pause for a split second to make sure.
		if(Math.abs(command.angle - ahrsGyro.getAngle()) < 3) {
			chassis.drive(0, 0);
			Timer.delay(0.1);
		}
		
		return (Math.abs(command.angle - ahrsGyro.getAngle()) < 3);
	}

	private void endCommand(int command) {
		if(command == AutonomousCommand.DO_NOTHING) return;
		else if(command == AutonomousCommand.DRIVE_STRAIGHT) chassis.drive(0, 0);
		else if(command == AutonomousCommand.ROTATE) chassis.drive(0, 0);
		else chassis.drive(0, 0); //Failsafe, just in case.
		
		timer.stop();
		timer.reset();
	}
	
	private int deriveRobotLocation() {
		if(robotPositionChooser.getSelected().equals("Left")) return LEFT;
		else if(robotPositionChooser.getSelected().equals("Center")) return CENTER;
		else if(robotPositionChooser.getSelected().equals("Right")) return RIGHT;
		
		return -1;
	}
	
	private int deriveScoringPosition(char gameDataPosition) {
		if(gameDataPosition == 'L') return LEFT;
		if(gameDataPosition == 'R') return RIGHT;
		return -1;
	}
	
	private boolean deriveSwitchSides() {
		return crossOverIfNecessary.getSelected().equals("Switch Sides To Reach Correct Color.");
	}
	
	public void teleopInit() {
		chassis.drive(0, 0);
//		autonomousRecorder.beginAutonomousRecording(robotControlScheme);
	}

	public void teleopPeriodic() {
//		updateAutonomousRecording();
//		robotControlScheme.updateXBoxOnly(xbox);
		System.out.println("Gyro Angle: " + ahrsGyro.getAngle());
		robotControlScheme.updateJoysticksAndXBox(leftJoystick, rightJoystick, xbox);
		robotControlScheme.updateRobot();
	}
	
	private void updateAutonomousRecording() {
		try {
			autonomousRecorder.updateFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void testPeriodic() {
		System.out.println("Gyro Angle: " + ahrsGyro.getAngle());
	}
}

