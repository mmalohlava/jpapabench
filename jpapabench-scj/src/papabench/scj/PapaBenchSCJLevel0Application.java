/**
 * 
 */
package papabench.scj;

import javax.realtime.PriorityParameters;
import javax.safetycritical.CyclicExecutive;
import javax.safetycritical.CyclicSchedule;
import javax.safetycritical.Mission;
import javax.safetycritical.MissionSequencer;
import javax.safetycritical.PeriodicEventHandler;
import javax.safetycritical.SingleMissionSequencer;
import javax.safetycritical.StorageParameters;
import javax.safetycritical.annotate.Level;

import papabench.core.PapaBench;
import papabench.core.commons.data.FlightPlan;
import papabench.core.commons.data.impl.Simple03FlightPlan;
import papabench.scj.schedule.SimplifiedPapabenchCyclicSchedule;

/**
 * The implementation of PapaBench based on SCJ Level 0.
 * 
 * The {@link CyclicExecutive} implements also a {@link Mission}.
 * 
 * @author Michal Malohlava
 *
 */
//@SCJAllowed(value=Level.LEVEL_0, members=true)
//@SCJAllowed(value=Level.LEVEL_0)
//@SCJAllowed
public class PapaBenchSCJLevel0Application extends CyclicExecutive {	
	
	public static final long MISSION_MEMORY_SIZE = 1000L;

	public PapaBenchSCJLevel0Application(StorageParameters storage) {
//		FIXME super(storage); ? should be called ?
	}
	
	@Override
	public CyclicSchedule getSchedule(PeriodicEventHandler[] pehs) {
		return SimplifiedPapabenchCyclicSchedule.generateSchedule(pehs);
	}

	@Override
	protected void initialize() {
		/*
		 * Creates an instance of PapaBench. 
		 */
		PapaBench papaBench = new PapaBenchSCJImpl();
		
		// setup flight plan for this mission
		papaBench.setFlightPlan(getFlightPlan());
		
		// initialize papabench 
		papaBench.init();
	}
	
	@Override
	public long missionMemorySize() {
		return MISSION_MEMORY_SIZE;
	}
	
	@Override
	public MissionSequencer getSequencer() {
		return new SingleMissionSequencer(new PriorityParameters(
				javax.realtime.PriorityScheduler.instance().getNormPriority()),
				new StorageParameters(100000L, 1000, 1000), this) {
			
			protected Mission getNextMission() {
				return null;
			}
		};
	}
	
	public void setup() {		
	}
	
	public void setUp() {		
	}

	public void tearDown() {		
	}
	
	public void teardown() {
	}
	
	@Override
	protected void cleanup() {
	}
		
	private FlightPlan getFlightPlan() {
		return new Simple03FlightPlan();
	}
	
	public Level getLevel() {
		return Level.LEVEL_0;		
	}		
}
