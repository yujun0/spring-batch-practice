package com.example._29_itemprocessor_composite;

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
import org.springframework.batch.item.support.CompositeItemProcessor;
import org.springframework.batch.item.validator.BeanValidatingItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.List;

// 讀取 user.txt 封裝 user 並打印
@SpringBootApplication
public class CompositeProcessorJob {
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
                .resource(new ClassPathResource("static/user-validate.txt"))
                // 解析數據 -> 指定解析器 使用 # 分割，默認是 ,
                .delimited().delimiter("#")
                .names("id", "name", "age")
                // 封裝數據 -> 將讀取的數據封裝成物件 User
                .targetType(User.class)
                .build();
    }

    @Bean
    public UserServiceImpl userService() {
        return new UserServiceImpl();
    }

    // BeanValidatingItemProcessor 是 ValidatingItemProcessor 子類
    @Bean
    public BeanValidatingItemProcessor<User> beanValidatingItemProcessor() {
        BeanValidatingItemProcessor itemProcessor = new BeanValidatingItemProcessor();
        itemProcessor.setFilter(true);   // 如果不滿足條件數據直接拋棄
        return itemProcessor;
    }

    @Bean
    public ItemProcessorAdapter<User, User> itemProcessorAdapter() {
        ItemProcessorAdapter<User, User> adapter = new ItemProcessorAdapter<>();
        adapter.setTargetObject(userService());
        adapter.setTargetMethod("toUpperCase");

        return adapter;
    }

    @Bean
    public CompositeItemProcessor<User, User> compositeProcessor() {
        CompositeItemProcessor<User, User> compositeItemProcessor = new CompositeItemProcessor<>();
        // 組合多個處理器
        compositeItemProcessor.setDelegates(List.of(beanValidatingItemProcessor(), itemProcessorAdapter()));

        return compositeItemProcessor;
    }

    @Bean
    public Step step() {
        return new StepBuilder("step1", jobRepository)
                .<User, User>chunk(1, transactionManager)   // 一次讀多少數據
                .reader(itemReader())
                .processor(compositeProcessor())
                .writer(itemWriter())
                .build();
    }

    @Bean
    public Job job() {
        return new JobBuilder("composite-processor-job", jobRepository)
                .start(step())
                .build();
    }

    public static void main(String[] args) {
        SpringApplication.run(CompositeProcessorJob.class, args);
    }
}
