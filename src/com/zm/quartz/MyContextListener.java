package com.zm.quartz;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Application Lifecycle Listener implementation class AListener
 *
 */
public class MyContextListener implements ServletContextListener {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    public static Scheduler scheduler = null;

    @Override
    public void contextInitialized(ServletContextEvent arg0) {
        this.logger.info("The application start...");

        /* 注册定时任务 */
        try {
            // 获取Scheduler实例
            scheduler = StdSchedulerFactory.getDefaultScheduler();
            scheduler.start();

            // 具体任务
            JobDetail job = JobBuilder.newJob(TrainJob.class).withIdentity("job1", "group1").build();

            // 触发时间点
            SimpleScheduleBuilder simpleScheduleBuilder = SimpleScheduleBuilder.simpleSchedule()
                    .withIntervalInHours(2).repeatForever();
            Trigger trigger = TriggerBuilder.newTrigger().withIdentity("trigger1", "group1")
                    .startNow().withSchedule(simpleScheduleBuilder).build();

            // 交由Scheduler安排触发
            scheduler.scheduleJob(job, trigger);

            this.logger.info("The scheduler register...");
        } catch (SchedulerException se) {
            logger.error(se.getMessage(), se);
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent arg0) {
        this.logger.info("The application stop...");

        /* 注销定时任务 */
        try {
            // 关闭Scheduler
            scheduler.shutdown();

            this.logger.info("The scheduler shutdown...");
        } catch (SchedulerException se) {
            logger.error(se.getMessage(), se);
        }
    }

}
