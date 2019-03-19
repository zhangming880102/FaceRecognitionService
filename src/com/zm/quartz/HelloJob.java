package com.zm.quartz;

import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HelloJob implements Job {

    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        // 接受参数
        JobDataMap jobDataMap = context.getJobDetail().getJobDataMap();
        // 通过这种方式，传递参数
        String taskId = jobDataMap.getString("taskId");
        System.out.println("here");
        // 此任务仅打印日志便于调试、观察
        this.logger.debug(this.getClass().getName() + " trigger..." + taskId);
    }

}