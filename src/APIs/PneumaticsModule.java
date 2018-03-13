package APIs;

import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.Solenoid;

public class PneumaticsModule {
	Compressor compressor;
	
	Solenoid solenoidLeft;
	Solenoid solenoidRight;
	
	public PneumaticsModule(int solenoidLeftPort, int solenoidRightPort) {
		compressor = new Compressor(0);
		
		solenoidLeft = new Solenoid(solenoidLeftPort);
		solenoidRight = new Solenoid(solenoidRightPort);
	}
	
	public void update() {
		if(compressor.getPressureSwitchValue()) deactivate();
		else activate();
	}
	
	public void activate() {
		compressor.start();
	}
	
	public void deactivate() {
		compressor.stop();
	}
	
	public void thrustLeft() {
		solenoidLeft.set(true);
		solenoidRight.set(false);
	}
	
	public void thrustRight() {
		solenoidLeft.set(false);
		solenoidRight.set(true);
	}
	
	public void toggleThrust() {
		solenoidLeft.set(!solenoidLeft.get());
		solenoidRight.set(!solenoidRight.get());
	}
}
