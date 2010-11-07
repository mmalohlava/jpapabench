/**
 * 
 */
package papabench.scj.utils;

import javax.realtime.PeriodicParameters;
import javax.realtime.PriorityParameters;
import javax.realtime.RelativeTime;
import javax.realtime.ReleaseParameters;

/**
 * This factory produces {@link ReleaseParameters} for specified values.
 * 
 * @author Michal Malohlava
 *
 */
//@SCJRestricted(INITIALIZATION)
//@SCJAllowed
final public class ParametersFactory {
	
	public static PriorityParameters getPriorityParameters(int priority) {
		return new PriorityParameters(priority);		
	}
	
	public static PeriodicParameters getPeriodicParameters(long startInMilis, long periodInMilis) {
		return new PeriodicParameters(new RelativeTime(startInMilis, 0), new RelativeTime(periodInMilis, 0));
	}
	
	public static RelativeTime getRelativeTime(long milis) {
		return new RelativeTime(milis, 0);
	}

}
