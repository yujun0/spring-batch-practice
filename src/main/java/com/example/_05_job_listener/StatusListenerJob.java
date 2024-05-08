package com.example._05_job_listener;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.listener.JobListenerFactoryBean;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.PlatformTransactionManager;

@SpringBootApplication
public class StatusListenerJob {

    // 創建一個 step 要執行的任務
    @Bean
    public Tasklet tasklet() {
        return (contribution, chunkContext) -> {

            JobExecution jobExecution = contribution.getStepExecution().getJobExecution();
            System.out.println("執行中-status:" + jobExecution.getStatus());
            return RepeatStatus.FINISHED;
        };
    }

    @Bean
    public JobStateListener jobStateListener() {
        return new JobStateListener();
    }


    @Bean
    public Step step1(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("step1", jobRepository)
                .tasklet(tasklet(), transactionManager)
                .build();
    }

    @Bean
    public Job job(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new JobBuilder("job-status-annotation-listener-job", jobRepository)
                .start(step1(jobRepository, transactionManager))
                // .listener(jobStateListener())
                .listener(JobListenerFactoryBean.getListener(new JobStateAnnotationListener()))
                .build();
    }

    public static void main(String[] args) {
        SpringApplication.run(StatusListenerJob.class, args);
    }
}
