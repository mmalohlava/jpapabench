/**
 * 
 */
package papabench.scj;

import papabench.core.PapaBench;
import papabench.core.autopilot.modules.AutopilotModule;
import papabench.core.bus.SPIBusChannel;
import papabench.core.commons.data.FlightPlan;
import papabench.core.fbw.modules.FBWModule;
import papabench.core.simulator.model.FlightModel;
import papabench.core.utils.LogUtils;

/**
 * Representation of a whole PapaBench system. 
 * 
 * Creates and initialize autopilot and fly-by-wire modules, registers their tasks and interrupts.
 * Also, create an environment simulator and registers its tasks.
 * 
 * @see AutopilotModule
 * @see FBWModule
 * @see FlightModel
 * 
 * @author Michal Malohlava
 *
 */
public class PapaBenchSCJImpl implements PapaBench {
	
	private AutopilotModule autopilotModule;
	
	private FBWModule fbwModule;	
	
	private FlightPlan flightPlan;
	
	public void init() {
		// Allocate and initialize global objects: 
		//  - MC0 - autopilot
		autopilotModule = PapaBenchSCJFactory.createAutopilotModule(this);
				
		//  - MC1 - FBW
		fbwModule = PapaBenchSCJFactory.createFBWModule();		
		
		// Create SPIBusChannel and connect both modules
		SPIBusChannel spiBusChannel = PapaBenchSCJFactory.createSPIBusChannel();
		spiBusChannel.init();
		autopilotModule.setSPIBus(spiBusChannel.getMasterEnd()); // = MC0: SPI master mode
		fbwModule.setSPIBus(spiBusChannel.getSlaveEnd()); // = MC1: SPI slave mode
		
		// setup flight plan
		LogUtils.log(this, "Flight plan: " + flightPlan.getName());
		autopilotModule.getNavigator().setFlightPlan(this.flightPlan);
		
		// initialize both modules - if the modules are badly initialized the runtime exception is thrown
		autopilotModule.init();
		fbwModule.init();
		
		// Register interrupt handlers
		PapaBenchSCJFactory.registerAutopilotInterruptHandlers(autopilotModule);
		PapaBenchSCJFactory.registerFBWInterruptHandlers(fbwModule);
		
		// Register period threads
		PapaBenchSCJFactory.registerAutopilotPeriodicTasks(autopilotModule);
		PapaBenchSCJFactory.registerFBWPeriodicTasks(fbwModule);
		
		// Create simulator
		FlightModel flightModel = PapaBenchSCJFactory.createSimulator();
		flightModel.init();
		// Register simulator tasks
		PapaBenchSCJFactory.registerSimulatorPeriodicTasks(flightModel, autopilotModule, fbwModule);				
	}

	public AutopilotModule getAutopilotModule() {
		return this.autopilotModule;
	}

	public FBWModule getFBWModule() {
		return this.fbwModule;
	}

	public void setFlightPlan(FlightPlan flightPlan) {
		this.flightPlan = flightPlan;
	}
	
	@Override
	public void shutdown() {
		// TODO Auto-generated method stub
		
	}

}
