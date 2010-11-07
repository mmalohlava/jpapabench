/**
 * 
 */
package papabench.scj;

import javax.realtime.PriorityParameters;
import javax.realtime.PriorityScheduler;
import javax.safetycritical.Mission;
import javax.safetycritical.MissionSequencer;
import javax.safetycritical.Safelet;
import javax.safetycritical.SingleMissionSequencer;
import javax.safetycritical.annotate.Level;

import papabench.core.PapaBench;
import papabench.core.commons.data.FlightPlan;
import papabench.core.commons.data.impl.Simple03FlightPlan;

/**
 * SCJ Level 1 PapaBench mission.
 *  
 * @author Michal Malohlava
 * 
 */
public class PapaBenchSCJLevel1Application extends Mission implements Safelet {

	public static final long PAPABENCH_MISSION_L1_SIZE = 1000L;

	public MissionSequencer getSequencer() {

		return new SingleMissionSequencer(new PriorityParameters(
				PriorityScheduler.instance().getNormPriority()), 
				null, 
				this) {
			protected Mission getNextMission() {
				return null;
			}
		};

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
		return PAPABENCH_MISSION_L1_SIZE;
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
		return Level.LEVEL_1;		
	}

}
