package com.example._02_params;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.PlatformTransactionManager;

@SpringBootApplication
public class ParamJob {

    // 創建一個 step 要執行的任務
    @StepScope
    @Bean
    public Tasklet tasklet(@Value("#{jobParameters['name']}") String name) {
        return (contribution, chunkContext) -> {
            // 方案 1：使用 chunkContext
            // Map<String, Object> jobParameters = chunkContext.getStepContext().getJobParameters();
            // System.out.println("params---name:" + jobParameters.get("name"));

            // 方案 2：使用 @Value
            System.out.println("params---name:" + name);
            return RepeatStatus.FINISHED;
        };
    }

    @Bean
    public Step step1(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("step1", jobRepository)
                .tasklet(tasklet(null), transactionManager)
                .build();
    }

    /* @Bean
    public Job job(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new JobBuilder("param-job", jobRepository)
                .start(step1(jobRepository, transactionManager))
                .build();
    } */

    /* @Bean
    public Job job(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new JobBuilder("param-chunk-job", jobRepository)
                .start(step1(jobRepository, transactionManager))
                .build();
    } */

    @Bean
    public Job job(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new JobBuilder("param-value-job", jobRepository)
                .start(step1(jobRepository, transactionManager))
                .build();
    }

    public static void main(String[] args) {
        SpringApplication.run(ParamJob.class, args);
    }
}
