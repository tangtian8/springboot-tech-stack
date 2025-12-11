package top.tangtian.misfires;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import java.util.Date;
import java.util.Properties;

import static org.quartz.JobBuilder.newJob;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import static org.quartz.TriggerBuilder.newTrigger;

/**
 * @author tangtian
 * @date 2025-12-08 09:38
 */
public class MisfireExample {
	public static void main(String[] args) throws SchedulerException, InterruptedException {
		Properties props = new Properties();

		// 基本配置
		props.setProperty("org.quartz.scheduler.instanceName", "MyScheduler");
		props.setProperty("org.quartz.scheduler.instanceId", "AUTO");

		// 线程池配置
		props.setProperty("org.quartz.threadPool.class", "org.quartz.simpl.SimpleThreadPool");
		props.setProperty("org.quartz.threadPool.threadCount", "3");
		props.setProperty("org.quartz.threadPool.threadPriority", "5");

		// JobStore 配置
		props.setProperty("org.quartz.jobStore.class", "org.quartz.simpl.RAMJobStore");

		// ⭐ 关键配置：设置 misfire 阈值为 5 秒（5000 毫秒）
		props.setProperty("org.quartz.jobStore.misfireThreshold", "2000");

		// 创建 SchedulerFactory
		StdSchedulerFactory factory = new StdSchedulerFactory();
		factory.initialize(props);
		Scheduler sched = factory.getScheduler();
		//Job #1：执行完一次后，会等到下一个"正常"的 3 秒间隔点再执行（跳过了中间错过的触发点）
	JobDetail job = newJob(StatefulDumbJob.class)
				.withIdentity("statefulJob1", "group1")
				.usingJobData(StatefulDumbJob.EXECUTION_DELAY, 10000L)
				.build();

		SimpleTrigger trigger = newTrigger()
				.withIdentity("trigger1", "group1")
				.startAt(new Date())
				.withSchedule(simpleSchedule()
						.withIntervalInSeconds(3)
						.repeatForever())
				.build();

		sched.scheduleJob(job, trigger);

		//Job #2：执行完一次后，会立即执行积压的任务（尽快补上错过的执行）
//		JobDetail job = newJob(StatefulDumbJob.class)
//				.withIdentity("statefulJob2", "group1")
//				.usingJobData(StatefulDumbJob.EXECUTION_DELAY, 10000L)
//				.build();
//
//		SimpleTrigger trigger = newTrigger()
//				.withIdentity("trigger2", "group1")
//				.startAt(new Date())
//				.withSchedule(simpleSchedule()
//						.withIntervalInSeconds(3)
//						.repeatForever()
//						.withMisfireHandlingInstructionNowWithExistingCount()) // set misfire instruction
//				.build();
//
//		sched.scheduleJob(job, trigger);


		sched.start();
		Thread.sleep(600L * 1000L);
		sched.shutdown(true);

	}
}
