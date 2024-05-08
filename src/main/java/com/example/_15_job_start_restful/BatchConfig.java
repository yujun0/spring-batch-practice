package com.example._15_job_start_restful;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class BatchConfig {

    @Autowired
    JobRepository jobRepository;

    @Autowired
    PlatformTransactionManager transactionManager;


    // 創建一個 step 要執行的任務
    @Bean
    public Tasklet tasklet() {
        return (contribution, chunkContext) -> {
            // 要執行的邏輯
            System.out.println("hello spring batch! ---> id: " + chunkContext.getStepContext().getJobParameters().get("id"));
            return RepeatStatus.FINISHED;
        };
    }

    @Bean
    public Step step1() {
        return new StepBuilder("step1", jobRepository)
                .tasklet(tasklet(), transactionManager)
                .build();
    }

    @Bean
    public Job job() {
        return new JobBuilder("hello-restful-job", jobRepository)
                .start(step1())
                .incrementer(new RunIdIncrementer())
                .build();
    }

}
