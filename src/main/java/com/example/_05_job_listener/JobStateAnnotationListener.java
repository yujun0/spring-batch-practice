package com.example._05_job_listener;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.annotation.AfterJob;
import org.springframework.batch.core.annotation.BeforeJob;

public class JobStateAnnotationListener {
    @BeforeJob
    public void beforeJob(JobExecution jobExecution) {
        System.out.println("執行前-status：" + jobExecution.getStatus());
    }

    @AfterJob
    public void afterJob(JobExecution jobExecution) {
        System.out.println("執行後-status：" + jobExecution.getStatus());
    }

}
