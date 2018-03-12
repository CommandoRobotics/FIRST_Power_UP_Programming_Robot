package configurations;

import APIs.Chassis;
import APIs.PneumaticsModule;

public class ProgrammingRobotConfiguration extends RobotConfiguration {
	public ProgrammingRobotConfiguration(Chassis chassis, PneumaticsModule pneumaticsModule) {
		this.addChassis(chassis);
		this.addPneumaticsModule(pneumaticsModule);
	}
}
