package com.example._11_step_condition_decider;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.PlatformTransactionManager;

@SpringBootApplication
public class CustomizeStepJob {
    // 創建一個 step 要執行的任務
    @Bean
    public Tasklet firstTasklet() {
        return (contribution, chunkContext) -> {
            System.out.println("----------firstTasklet----------");

            // return RepeatStatus.CONTINUABLE; // 迴圈執行
            return RepeatStatus.FINISHED;
        };
    }

    @Bean
    public Tasklet taskletA() {
        return (contribution, chunkContext) -> {
            System.out.println("----------taskletA----------");

            // return RepeatStatus.CONTINUABLE; // 迴圈執行
            return RepeatStatus.FINISHED;
        };
    }

    @Bean
    public Tasklet taskletB() {
        return (contribution, chunkContext) -> {
            System.out.println("----------taskletB----------");

            // return RepeatStatus.CONTINUABLE; // 迴圈執行
            return RepeatStatus.FINISHED;
        };
    }

    @Bean
    public Tasklet taskletDefault() {
        return (contribution, chunkContext) -> {
            System.out.println("----------taskletDefault----------");

            // return RepeatStatus.CONTINUABLE; // 迴圈執行
            return RepeatStatus.FINISHED;
        };
    }

    @Bean
    public MyStatusDecider statusDecider() {
        return new MyStatusDecider();
    }

    @Bean
    public Step firstStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("firstStep", jobRepository)
                .tasklet(firstTasklet(), transactionManager)
                .build();
    }

    @Bean
    public Step stepA(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("stepA", jobRepository)
                .tasklet(taskletA(), transactionManager)
                .build();
    }

    @Bean
    public Step stepB(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("stepB", jobRepository)
                .tasklet(taskletB(), transactionManager)
                .build();
    }

    @Bean
    public Step stepDefault(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("stepDefault", jobRepository)
                .tasklet(taskletDefault(), transactionManager)
                .build();
    }

    @Bean
    public Job job(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new JobBuilder("customize-step-job", jobRepository)
                .start(firstStep(jobRepository, transactionManager))
                .next(statusDecider())
                .from(statusDecider()).on("A").to(stepA(jobRepository, transactionManager))
                .from(statusDecider()).on("B").to(stepB(jobRepository, transactionManager))
                .from(statusDecider()).on("*").to(stepDefault(jobRepository, transactionManager))
                .end()
                .incrementer(new RunIdIncrementer())
                .build();
    }

    public static void main(String[] args) {
        SpringApplication.run(CustomizeStepJob.class, args);
    }
}
