package com.example._03_param_validator;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.CompositeJobParametersValidator;
import org.springframework.batch.core.job.DefaultJobParametersValidator;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.List;
import java.util.Map;

@SpringBootApplication
public class ParamValidatorJob {

    // 創建一個 step 要執行的任務
    @StepScope
    @Bean
    public Tasklet tasklet() {
        return (contribution, chunkContext) -> {
            // 方案 1：使用 chunkContext
            Map<String, Object> jobParameters = chunkContext.getStepContext().getJobParameters();
            System.out.println("params--必填--name:" + jobParameters.get("name"));
            System.out.println("params--可選--age:" + jobParameters.get("age"));

            return RepeatStatus.FINISHED;
        };
    }

    @Bean
    public Step step1(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("step1", jobRepository)
                .tasklet(tasklet(), transactionManager)
                .build();
    }

    // 自定義參數校驗器
    @Bean
    public NameParamValidator nameParamValidator() {
        return new NameParamValidator();
    }

    @Bean
    public DefaultJobParametersValidator defaultJobParametersValidator() {
        DefaultJobParametersValidator validator = new DefaultJobParametersValidator();
        // not null 參數
        validator.setRequiredKeys(new String[]{"name"});    // 必須傳name參數
        // 可選參數
        validator.setOptionalKeys(new String[]{"age"});     // age 可填可不填
        return validator;
    }

    // 組合參數組合器
    @Bean
    public CompositeJobParametersValidator compositeJobParametersValidator() throws Exception {
        CompositeJobParametersValidator validator = new CompositeJobParametersValidator();
        validator.setValidators(List.of(nameParamValidator(), defaultJobParametersValidator()));
        validator.afterPropertiesSet();

        return validator;
    }

    @Bean
    public Job job(JobRepository jobRepository, PlatformTransactionManager transactionManager) throws Exception {
        return new JobBuilder("composite-name-param-validator-job", jobRepository)
                .start(step1(jobRepository, transactionManager))
                .validator(compositeJobParametersValidator())    // 組合參數校驗器
                .build();
    }

    public static void main(String[] args) {
        SpringApplication.run(ParamValidatorJob.class, args);
    }
}
