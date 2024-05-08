package com.example._27_itemprocessor_adapter;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.adapter.ItemProcessorAdapter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.transaction.PlatformTransactionManager;

// 讀取 user.txt 封裝 user 並打印
@SpringBootApplication
public class AdapterProcessorJob {
    @Autowired
    private JobRepository jobRepository;
    @Autowired
    private PlatformTransactionManager transactionManager;

    @Bean
    public ItemWriter<User> itemWriter() {
        return new ItemWriter<User>() {
            @Override
            public void write(Chunk<? extends User> chunk) throws Exception {
                chunk.forEach(System.err::println);
            }
        };
    }

    @Bean
    public FlatFileItemReader<User> itemReader() {
        return new FlatFileItemReaderBuilder<User>()
                .name("userItemReader")
                // 獲取文件
                .resource(new ClassPathResource("static/user-adapter.txt"))
                // 解析數據 -> 指定解析器 使用 # 分割，默認是 ,
                .delimited().delimiter("#")
                .names("id", "name", "age")
                // 封裝數據 -> 將讀取的數據封裝成物件 User
                .targetType(User.class)
                .build();
    }

    // 已經定義好的 用戶名轉換類
    @Bean
    public UserServiceImpl userService() {
        return new UserServiceImpl();
    }

    @Bean
    public ItemProcessorAdapter<User, User> itemProcessorAdapter() {
        ItemProcessorAdapter<User, User> adapter = new ItemProcessorAdapter<>();

        adapter.setTargetMethod("toUpperCase");
        adapter.setTargetObject(userService());   // 找到適配的邏輯類

        return adapter;
    }

    @Bean
    public Step step() {
        return new StepBuilder("step1", jobRepository)
                .<User, User>chunk(1, transactionManager)   // 一次讀多少數據
                .reader(itemReader())
                .processor(itemProcessorAdapter())
                .writer(itemWriter())
                .build();
    }

    @Bean
    public Job job() {
        return new JobBuilder("adapter-processor-job", jobRepository)
                .start(step())
                .build();
    }

    public static void main(String[] args) {
        SpringApplication.run(AdapterProcessorJob.class, args);
    }
}
