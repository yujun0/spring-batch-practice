package com.example._16_job_stop;

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
public class ListenerJobStopJob {

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
            // return RepeatStatus.CONTINUABLE; // 迴圈執行
            return RepeatStatus.FINISHED;
        };
    }

    @Bean
    public Tasklet tasklet2() {
        return (contribution, chunkContext) -> {
            System.out.println("step2 執行了.........");
            System.out.println("readCount: " + ResourceCount.readCount + ", totalCount: " + ResourceCount.totalCount);
            System.out.printf("ff readCount: %d, totalCount: %d", ResourceCount.readCount, ResourceCount.totalCount);

            return RepeatStatus.FINISHED;
        };
    }

    @Bean
    public StopStepListener stopStepListener() {
        return new StopStepListener();
    }

    @Bean
    public Step step1() {
        return new StepBuilder("step1", jobRepository)
                .tasklet(tasklet1(), transactionManager)
                .listener(stopStepListener())
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
        return new JobBuilder("step-stop-job", jobRepository)
                .start(step1())
                // 當 step1 返回的是 STOPPED，馬上結束流程，設置 status 為 STOPPED，並設置重啟位置從 step1 開始執行
                .on("STOPPED").stopAndRestart(step1())
                // 如果 step1 返回不為 STOPPED，表示滿足判斷條件，執行 step2
                .from(step1()).on("*").to(step2())
                .end()
                .build();
    }

    public static void main(String[] args) {
        SpringApplication.run(ListenerJobStopJob.class, args);
    }
}
