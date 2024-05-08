package com.example._11_step_condition;

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
public class ConditionStepJob {
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
    public Tasklet successTasklet() {
        return (contribution, chunkContext) -> {
            System.out.println("----------sucessTasklet----------");

            // return RepeatStatus.CONTINUABLE; // 迴圈執行
            return RepeatStatus.FINISHED;
        };
    }

    @Bean
    public Tasklet failTasklet() {
        return (contribution, chunkContext) -> {
            System.out.println("----------failTasklet----------");

            // return RepeatStatus.CONTINUABLE; // 迴圈執行
            return RepeatStatus.FINISHED;
        };
    }

    @Bean
    public Step firstStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("firstStep", jobRepository)
                .tasklet(firstTasklet(), transactionManager)
                .build();
    }

    @Bean
    public Step successStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("successStep", jobRepository)
                .tasklet(successTasklet(), transactionManager)
                .build();
    }

    @Bean
    public Step failStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("failStep", jobRepository)
                .tasklet(failTasklet(), transactionManager)
                .build();
    }

    @Bean
    public Job job(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new JobBuilder("step-multi-job", jobRepository)
                .start(firstStep(jobRepository, transactionManager))
                .on("FAILED").to(failStep(jobRepository, transactionManager))
                .from(firstStep(jobRepository, transactionManager)).on("*").to(successStep(jobRepository, transactionManager))
                .end()
                .incrementer(new RunIdIncrementer())
                .build();
    }

    public static void main(String[] args) {
        SpringApplication.run(ConditionStepJob.class, args);
    }
}
