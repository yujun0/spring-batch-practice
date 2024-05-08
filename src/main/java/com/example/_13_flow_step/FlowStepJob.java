package com.example._13_flow_step;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.FlowBuilder;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.job.flow.Flow;
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
public class FlowStepJob {
    // 創建一個 step 要執行的任務
    @Bean
    public Tasklet taskletA() {
        return (contribution, chunkContext) -> {
            System.out.println("----------taskletA----------");

            return RepeatStatus.FINISHED;
        };
    }

    @Bean
    public Tasklet taskletC() {
        return (contribution, chunkContext) -> {
            System.out.println("----------taskletC----------");

            // return RepeatStatus.CONTINUABLE; // 迴圈執行
            return RepeatStatus.FINISHED;
        };
    }

    @Bean
    public Tasklet taskletB1() {
        return (contribution, chunkContext) -> {
            System.out.println("----------taskletB1----------");

            // return RepeatStatus.CONTINUABLE; // 迴圈執行
            return RepeatStatus.FINISHED;
        };
    }

    @Bean
    public Tasklet taskletB2() {
        return (contribution, chunkContext) -> {
            System.out.println("----------taskletB2----------");

            // return RepeatStatus.CONTINUABLE; // 迴圈執行
            return RepeatStatus.FINISHED;
        };
    }

    @Bean
    public Tasklet taskletB3() {
        return (contribution, chunkContext) -> {
            System.out.println("----------taskletB3----------");

            // return RepeatStatus.CONTINUABLE; // 迴圈執行
            return RepeatStatus.FINISHED;
        };
    }

    @Bean
    public Step stepA(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("stepA", jobRepository)
                .tasklet(taskletA(), transactionManager)
                .build();
    }

    // 構建一個 flowStep
    @Bean
    public Step stepB1(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("stepB1", jobRepository)
                .tasklet(taskletB1(), transactionManager)
                .build();
    }

    @Bean
    public Step stepB2(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("stepB2", jobRepository)
                .tasklet(taskletB2(), transactionManager)
                .build();
    }

    @Bean
    public Step stepB3(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("stepB3", jobRepository)
                .tasklet(taskletB3(), transactionManager)
                .build();
    }

    @Bean
    public Flow flowB(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new FlowBuilder<Flow>("flowB")
                .start(stepB1(jobRepository, transactionManager))
                .next(stepB2(jobRepository, transactionManager))
                .next(stepB3(jobRepository, transactionManager))
                .build();
    }

    // job 沒有現有的 flowStep 步驟操作方法，必須使用 step 進行封裝後再執行
    @Bean
    public Step stepB(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("stepB", jobRepository)
                .flow(flowB(jobRepository, transactionManager))
                .build();
    }

    @Bean
    public Step stepC(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("stepC", jobRepository)
                .tasklet(taskletC(), transactionManager)
                .build();
    }

    @Bean
    public Job job(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new JobBuilder("flow-step-job", jobRepository)
                .start(stepA(jobRepository, transactionManager))
                .next(stepB(jobRepository, transactionManager))
                .next(stepC(jobRepository, transactionManager))
                .incrementer(new RunIdIncrementer())
                .build();
    }

    public static void main(String[] args) {
        SpringApplication.run(FlowStepJob.class, args);
    }
}
