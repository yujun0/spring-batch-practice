package com.example._04_param_incr;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.Map;

@SpringBootApplication
public class IncrementParamJob {

    // 創建一個 step 要執行的任務
    @Bean
    public Tasklet tasklet() {
        return (contribution, chunkContext) -> {
            // 方案 1：使用 chunkContext
            Map<String, Object> jobParameters = chunkContext.getStepContext().getJobParameters();
            System.out.println("params---daily:" + jobParameters.get("daily"));
            return RepeatStatus.FINISHED;
        };
    }

    @Bean
    public DailyTimestampParamIncrementer dailyTimestampParamIncrementer() {
        return new DailyTimestampParamIncrementer();
    }

    @Bean
    public Step step1(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("step1", jobRepository)
                .tasklet(tasklet(), transactionManager)
                .build();
    }

    @Bean
    public Job job(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new JobBuilder("incr-params-job", jobRepository)
                .start(step1(jobRepository, transactionManager))
                .incrementer(dailyTimestampParamIncrementer())    // 時間戳自增
                .build();
    }

    public static void main(String[] args) {
        SpringApplication.run(IncrementParamJob.class, args);
    }
}
