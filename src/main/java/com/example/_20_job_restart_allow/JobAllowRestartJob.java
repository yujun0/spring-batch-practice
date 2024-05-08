package com.example._20_job_restart_allow;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.PlatformTransactionManager;

@SpringBootApplication
public class JobAllowRestartJob {

    @Autowired
    private JobRepository jobRepository;
    @Autowired
    private PlatformTransactionManager transactionManager;

    // 創建一個 step 要執行的任務
    @Bean
    public Tasklet tasklet1() {
        return (contribution, chunkContext) -> {
            System.err.println("----------tasklet1----------");
            return RepeatStatus.FINISHED;
        };
    }

    @Bean
    public Tasklet tasklet2() {
        return (contribution, chunkContext) -> {
            System.err.println("----------tasklet2----------");
            return RepeatStatus.FINISHED;
        };
    }


    @Bean
    public Step step1() {
        return new StepBuilder("step1", jobRepository)
                .tasklet(tasklet1(), transactionManager)
                .allowStartIfComplete(true)
                .build();
    }

    @Bean
    public Step step2() {
        return new StepBuilder("step2", jobRepository)
                .tasklet(tasklet2(), transactionManager)
                .build();
    }

    @Bean
    public Job job() {
        return new JobBuilder("job-allow-restart-job", jobRepository)
                .start(step1())
                .next(step2())
                .build();
    }

    public static void main(String[] args) {
        SpringApplication.run(JobAllowRestartJob.class, args);
    }
}
