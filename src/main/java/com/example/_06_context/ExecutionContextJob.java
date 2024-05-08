package com.example._06_context;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.PlatformTransactionManager;

@SpringBootApplication
public class ExecutionContextJob {

    // 創建一個 step 要執行的任務
    @Bean
    public Tasklet tasklet1() {
        return (contribution, chunkContext) -> {
            // Step
            // 可以獲取共享數據，但不允許修改
            // Map<String, Object> stepExecutionContext = chunkContext.getStepContext().getStepExecutionContext();
            // 透過執行 context 物件獲取及設置參數
            ExecutionContext stepEC = chunkContext.getStepContext().getStepExecution().getExecutionContext();
            stepEC.put("key-step1-step", "value-step1-step");

            System.out.println("----------1----------");
            // Job
            ExecutionContext jobEC = chunkContext.getStepContext().getStepExecution().getJobExecution().getExecutionContext();
            jobEC.put("key-step1-job", "value-step1-job");

            return RepeatStatus.FINISHED;
        };
    }

    @Bean
    public Tasklet tasklet2() {
        return (contribution, chunkContext) -> {
            // Step
            ExecutionContext stepEC = chunkContext.getStepContext().getStepExecution().getExecutionContext();
            System.err.println(stepEC.get("key-step1-step"));

            System.out.println("----------2----------");
            // Job
            ExecutionContext jobEC = chunkContext.getStepContext().getStepExecution().getJobExecution().getExecutionContext();
            System.err.println(jobEC.get("key-step1-job"));

            return RepeatStatus.FINISHED;
        };
    }

    @Bean
    public Step step1(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("step1", jobRepository)
                .tasklet(tasklet1(), transactionManager)
                .build();
    }

    @Bean
    public Step step2(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("step2", jobRepository)
                .tasklet(tasklet2(), transactionManager)
                .build();
    }

    @Bean
    public Job job(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new JobBuilder("api-execution-context-job", jobRepository)
                .start(step1(jobRepository, transactionManager))
                .next(step2(jobRepository, transactionManager))
                .incrementer(new RunIdIncrementer())
                .build();
    }

    public static void main(String[] args) {
        SpringApplication.run(ExecutionContextJob.class, args);
    }
}
