package autonomous_recorder;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import control_schemes.RobotControlScheme;

public class AutonomousRecorder {
	
	private boolean recordingAutonomousProgram;
	private BufferedWriter bufferedWriter;
	private File autonomousFile;
	
	private RobotControlScheme rcs;
	
	public AutonomousRecorder() {
		recordingAutonomousProgram = false;
		
		System.out.println(System.getProperty("user.dir"));
		
		autonomousFile = new File("C:\\FIRST Power UP Autonomous Programs\\Autonomous Test.txt");
		if(!autonomousFile.exists()) {
			try {
				autonomousFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		try {
			bufferedWriter = new BufferedWriter(new FileWriter(autonomousFile));
			bufferedWriter.write("Swago.");
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Failed to create buffered writer from autonomous file.");
		}
	}
	
	public void beginAutonomousRecording(RobotControlScheme robotControlScheme) {
		this.rcs = robotControlScheme;
		recordingAutonomousProgram = true;
	}
	
	public void updateFile() throws IOException {
		if(rcs.left_joystick_x > 0) bufferedWriter.write("ljx: " + rcs.left_joystick_x);
		if(rcs.left_joystick_y > 0) bufferedWriter.write("ljy: " + rcs.left_joystick_y);
		if(rcs.right_joystick_x > 0) bufferedWriter.write("rjx: " + rcs.right_joystick_x);
		if(rcs.right_joystick_y > 0) bufferedWriter.write("rjy: " + rcs.right_joystick_y);
		bufferedWriter.write("-----");
	} 
	
	public void stopAutonomousRecording() throws IOException {
		bufferedWriter.close();
	}
	
	public boolean getRecordingAutonomousProgram() {
		return recordingAutonomousProgram;
	}
}
