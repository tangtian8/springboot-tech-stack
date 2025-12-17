package top.tangtian.misfires;

import org.quartz.*;

import java.util.Date;

/**
 * @author tangtian
 * @date 2025-12-08 09:38
 */
@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class StatefulDumbJob implements Job {

	public static final String NUM_EXECUTIONS = "NumExecutions";
	public static final String EXECUTION_DELAY = "ExecutionDelay";

	public StatefulDumbJob() {
	}

	public void execute(JobExecutionContext context)
			throws JobExecutionException {
		System.err.println("---" + context.getJobDetail().getKey()
				+ " executing.[" + new Date() + "]");

		JobDataMap map = context.getJobDetail().getJobDataMap();

		int executeCount = 0;
		if (map.containsKey(NUM_EXECUTIONS)) {
			executeCount = map.getInt(NUM_EXECUTIONS);
		}

		executeCount++;
		map.put(NUM_EXECUTIONS, executeCount);

		long delay = 5000l;
		if (map.containsKey(EXECUTION_DELAY)) {
			delay = map.getLong(EXECUTION_DELAY);
		}

		try {
			Thread.sleep(delay);
		} catch (Exception ignore) {
		}

		System.err.println("  -" + context.getJobDetail().getKey()
				+ " complete (" + executeCount + ").");
	}
}

