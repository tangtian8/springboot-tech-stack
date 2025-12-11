package top.tangtian.jobstateexample;

import org.quartz.*;

import java.util.Date;

/**
 * @author tangtian
 * @date 2025-12-08 09:15
 */
@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class ColorJob implements Job {

	public static final String EXECUTION_COUNT = "_counter";
	public static final String FAVORITE_COLOR = "FAVORITE_COLOR";


	@Override
	public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
		JobDataMap data = jobExecutionContext.getJobDetail().getJobDataMap();
		String favoriteColor = data.getString(FAVORITE_COLOR);
		int count = data.getInt(EXECUTION_COUNT);
		count++;
		data.put(EXECUTION_COUNT, count);
		String name = jobExecutionContext.getJobDetail().getKey().getName();
		System.out.println("ColorJob: " + name + " executing at " + new Date() + "\n" +
				"  favorite color is " + favoriteColor + "\n" +
				"  execution count (from job map) is " + count + "\n" +
				"  execution count (from job member variable) is " + EXECUTION_COUNT);
	}
}
