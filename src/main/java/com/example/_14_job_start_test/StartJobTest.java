package com.example._14_job_start_test;

import org.junit.jupiter.api.Test;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.PlatformTransactionManager;

@SpringBatchTest
@SpringBootTest
public class StartJobTest {

    @Autowired
    JobRepository jobRepository;

    @Autowired
    PlatformTransactionManager transactionManager;

    @Autowired
    JobLauncherTestUtils jobLauncherTestUtils;

    // 創建一個 step 要執行的任務
    public Tasklet tasklet() {
        return (contribution, chunkContext) -> {
            // 要執行的邏輯
            System.out.println("hello spring batch!");
            return RepeatStatus.FINISHED;
        };
    }

    public Step step1() {
        return new StepBuilder("step1", jobRepository)
                .tasklet(tasklet(), transactionManager)
                .build();
    }

    // 在測試類別中創建 Job
    public Job job() {
        return new JobBuilder("start-test-job", jobRepository)
                .start(step1())
                .build();
    }

    @Test
    public void testStart() throws Exception {
        jobLauncherTestUtils.setJob(job());
        jobLauncherTestUtils.launchJob();
    }

}
