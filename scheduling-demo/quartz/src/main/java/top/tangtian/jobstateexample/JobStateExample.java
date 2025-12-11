package top.tangtian.jobstateexample;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import java.util.Date;

import static org.quartz.JobBuilder.newJob;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import static org.quartz.TriggerBuilder.newTrigger;

/**
 * @author tangtian
 * @date 2025-12-08 09:15
 */
public class JobStateExample {
	public static void main(String[] args) throws SchedulerException, InterruptedException {
		SchedulerFactory sf = new StdSchedulerFactory();
		Scheduler sched = sf.getScheduler();

		JobDetail job1 = newJob(ColorJob.class)
				.withIdentity("job1", "group1")
				.build();
		job1.getJobDataMap().put(ColorJob.FAVORITE_COLOR, "Green");
		job1.getJobDataMap().put(ColorJob.EXECUTION_COUNT, 1);

		SimpleTrigger trigger1 = newTrigger()
				.withIdentity("trigger1", "group1")
				.startAt(new Date())
				.withSchedule(simpleSchedule()
						.withIntervalInSeconds(10)
						.withRepeatCount(4))
				.build();

		sched.scheduleJob(job1, trigger1);



		JobDetail job2 = newJob(ColorJob.class)
				.withIdentity("job2", "group1")
				.build();

		SimpleTrigger trigger2 = newTrigger()
				.withIdentity("trigger2", "group1")
				.startAt(new Date())
				.withSchedule(simpleSchedule()
						.withIntervalInSeconds(10)
						.withRepeatCount(4))
				.build();

		job2.getJobDataMap().put(ColorJob.FAVORITE_COLOR, "Red");
		job2.getJobDataMap().put(ColorJob.EXECUTION_COUNT, 1);
		sched.scheduleJob(job2, trigger2);

		sched.start();
		Thread.sleep(60L * 1000L);
		sched.shutdown(true);

	}
}
