package top.tangtian;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.util.Date;

/**
 * @author tangtian
 * @date 2025-12-08 09:08
 */

public class HelloJob implements Job {
	@Override
	public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
		System.out.println(    "Hello World! - " + new Date());
	}
}
