/**
 * 
 */
package papabench.scj.autopilot.interrupts;

import javax.realtime.PeriodicParameters;
import javax.realtime.PriorityParameters;
import javax.safetycritical.PeriodicEventHandler;
import javax.safetycritical.StorageParameters;

/**
 * 
 * NOTE: GPS device produces a stream of GPS positions (according to AADL
 * definition), however, this code simplifies it to produce {@link GPSPosition}
 * only every period.
 * 
 * Period: T = 250ms
 * 
 * @author Michal Malohlava
 * 
 */
public class GPSIntHandler extends PeriodicEventHandler {

	public GPSIntHandler(PriorityParameters priority,
			PeriodicParameters period, StorageParameters storage, long size) {
		super(priority, period, storage, size);		
	}

	@Override
	public void handleAsyncEvent() {
	}

	public void cleanUp() {
	}

	public void register() {
	}

	public StorageParameters getThreadConfigurationParameters() {
		return null;
	}
	
}
