package configurations;

import APIs.Chassis;
import APIs.PneumaticsModule;

public class RobotConfiguration {
	private boolean chassisAdded, pneumaticsAdded;
	private Chassis chassis;
	private PneumaticsModule pneumaticsModule;
	
	public RobotConfiguration() {
		chassisAdded = false;
		pneumaticsAdded = false;
	}
	
	public void addChassis(Chassis chassis) {
		chassisAdded = true;
		this.chassis = chassis;
	}
	
	public void updateChassisLeftSide(double leftX, double leftY) {
		chassis.driveLeftSide(leftY);
	}
	
	public void updateChassisRightSide(double rightX, double rightY) {
		chassis.driveRightSide(rightY);
	}
	
	public void updateChassis(double leftX, double leftY, double rightX, double rightY) {
		System.out.println("Chassis Added: " + chassisAdded);
		if(!chassisAdded) return;
		chassis.drive(leftY, rightY);
	}
	
	public void addPneumaticsModule(PneumaticsModule pneumaticsModule) {
		pneumaticsAdded = true;
		this.pneumaticsModule = pneumaticsModule;
	}
	
	public void updatePneumatics() {
		if(!pneumaticsAdded) return;
		pneumaticsModule.update();
	}
	
	public void togglePneumatics(boolean toggle) {
		if(toggle && pneumaticsAdded) pneumaticsModule.toggleThrust();
	}
}
