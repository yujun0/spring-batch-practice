package com.example._17_job_stop_sign;

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
public class SignJobStopJob {

    @Autowired
    private JobRepository jobRepository;
    @Autowired
    private PlatformTransactionManager transactionManager;

    // 模擬從資料庫查詢資料
    private int readCountDB = 100;

    // 創建一個 step 要執行的任務
    @Bean
    public Tasklet tasklet1() {
        return (contribution, chunkContext) -> {
            System.out.println("----------step1----------");
            for (int i = 1; i <= readCountDB; i++) {
                ResourceCount.readCount++;  // 50
            }

            // 如果不滿足條件： readCount != totalCount 設置停止標記
            if (ResourceCount.readCount != ResourceCount.totalCount) {
                // 停止標記
                chunkContext.getStepContext().getStepExecution().setTerminateOnly();
            }
            return RepeatStatus.FINISHED;
        };
    }

    @Bean
    public Tasklet tasklet2() {
        return (contribution, chunkContext) -> {
            System.out.println("step2 執行了.........");
            System.out.printf("readCount: %d, totalCount: %d \n", ResourceCount.readCount, ResourceCount.totalCount);

            return RepeatStatus.FINISHED;
        };
    }


    @Bean
    public Step step1() {
        return new StepBuilder("step1", jobRepository)
                .tasklet(tasklet1(), transactionManager)
                .allowStartIfComplete(true) // 允許 step 重新執行
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
        return new JobBuilder("sign-step-stop-job", jobRepository)
                .start(step1())
                .next(step2())
                .build();
    }

    public static void main(String[] args) {
        SpringApplication.run(SignJobStopJob.class, args);
    }
}
