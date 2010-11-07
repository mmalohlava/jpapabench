/**
 * 
 */
package papabench.scj.commons.tasks;

import static papabench.scj.utils.ParametersFactory.getPeriodicParameters;
import static papabench.scj.utils.ParametersFactory.getPriorityParameters;

import javax.safetycritical.PeriodicEventHandler;

import papabench.core.commons.conf.CommonTaskConfiguration;
import papabench.core.utils.LogUtils;

/**
 * PeriodicEventHandler wrapper.
 * 
 * @author Michal Malohlava
 *
 */
public class PapaBenchSCJPeriodicTask extends PeriodicEventHandler implements CommonTaskConfiguration {
	
	private Runnable targetRunnable;
	
	private String name;
	
	/* 
	 * Static variable to distinguish between two repetition of PapaBench schedule
	 * 
	 * Required by JPF.
	 */	
	public static int PAPABENCH_TASKS_EXECUTION_COUNTER = 0;

	public PapaBenchSCJPeriodicTask(Runnable targetRunnable, int priority, int releaseMs, int periodMs, long memSize, String name){
		super(getPriorityParameters(priority), getPeriodicParameters(releaseMs, periodMs), null, memSize);
				
		this.targetRunnable = targetRunnable;
		this.name = name;
	}
	
	@Override
	final public void handleAsyncEvent() {
		LogUtils.log(getTaskName(), "PERIOD handler enter");		
		
		try {
			PAPABENCH_TASKS_EXECUTION_COUNTER++;
			
			targetRunnable.run();			
		} finally {		
			LogUtils.log(getTaskName(), "PERIOD handler return");
		}
	}

	@Override
	public String getTaskName() {
		return this.name;		
	}	
}
