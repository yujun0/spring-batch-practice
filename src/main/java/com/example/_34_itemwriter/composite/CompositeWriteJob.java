package com.example._34_itemwriter.composite;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.batch.item.json.JacksonJsonObjectMarshaller;
import org.springframework.batch.item.json.JsonFileItemWriter;
import org.springframework.batch.item.json.builder.JsonFileItemWriterBuilder;
import org.springframework.batch.item.support.CompositeItemWriter;
import org.springframework.batch.item.support.builder.CompositeItemWriterBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.PathResource;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.List;

@SpringBootApplication
public class CompositeWriteJob {
    @Autowired
    private JobRepository jobRepository;
    @Autowired
    private PlatformTransactionManager transactionManager;
    @Autowired
    private DataSource dataSource;

    @Bean
    public ItemWriter<User> consoleItemWriter() {
        return new ItemWriter<User>() {
            @Override
            public void write(Chunk<? extends User> chunk) throws Exception {
                chunk.forEach(System.err::println);
            }
        };
    }

    @Bean
    public FlatFileItemWriter<User> flatFileItemWriter() {
        return new FlatFileItemWriterBuilder<User>()
                .name("userFlatItemWriter")
                .resource(new PathResource("D:/outUser.txt"))  // 輸出的文件
                .formatted()  // 數據格式指定
                .format("id: %s, 姓名: %s, 年齡: %s")  // 輸出數據格是
                .names("id", "name", "age")  // 需要輸出屬性
                .shouldDeleteIfEmpty(true)  // 如果讀入數據為空，輸出時創建文件直接刪除
                .shouldDeleteIfExists(true) // 如果輸出文件已經存在，則刪除
                .append(true)  // 如果輸出文件已經存在，不刪除，直接追加到現有文件中
                .build();
    }

    @Bean
    public FlatFileItemReader<User> fileItemReader() {
        return new FlatFileItemReaderBuilder<User>()
                .name("userItemReader")
                // 獲取文件
                .resource(new ClassPathResource("static/user.txt"))
                // 解析數據 -> 指定解析器 使用 # 分割，默認是 ,
                .delimited().delimiter("#")
                .names("id", "name", "age")
                // 封裝數據 -> 將讀取的數據封裝成物件 User
                .targetType(User.class)
                .build();
    }

    @Bean
    public JacksonJsonObjectMarshaller<User> objectMarshaller() {
        return new JacksonJsonObjectMarshaller();
    }

    @Bean
    public JsonFileItemWriter<User> jsonFileItemWriter() {
        return new JsonFileItemWriterBuilder<User>()
                .name("userJsonItemWriter")
                .resource(new PathResource("D:/outUser.json"))  // 輸出的文件
                .jsonObjectMarshaller(objectMarshaller())
                .build();
    }

    @Bean
    public UserPreStatementSetter userPreStatementSetter() {
        return new UserPreStatementSetter();
    }

    @Bean
    public JdbcBatchItemWriter<User> jdbcBatchItemWriter() {
        return new JdbcBatchItemWriterBuilder<User>()
                .dataSource(dataSource)
                .sql("INSERT INTO user VALUES (?, ?, ?);")
                .itemPreparedStatementSetter(userPreStatementSetter())
                .build();
    }

    @Bean
    public FlatFileItemReader<User> itemReader() {
        return new FlatFileItemReaderBuilder<User>()
                .name("userItemReader")
                // 獲取文件
                .resource(new ClassPathResource("static/user.txt"))
                // 解析數據 -> 指定解析器 使用 # 分割，默認是 ,
                .delimited().delimiter("#")
                .names("id", "name", "age")
                // 封裝數據 -> 將讀取的數據封裝成物件 User
                .targetType(User.class)
                .build();
    }

    @Bean
    public CompositeItemWriter<User> compositeItemWriter() {
        return new CompositeItemWriterBuilder<User>()
                .delegates(List.of(consoleItemWriter(), flatFileItemWriter(), jsonFileItemWriter(), jdbcBatchItemWriter()))
                .build();
    }

    @Bean
    public Step step() {
        return new StepBuilder("step1", jobRepository)
                .<User, User>chunk(1, transactionManager)   // 一次讀多少數據
                .reader(itemReader())
                .writer(compositeItemWriter())
                .build();
    }

    @Bean
    public Job job() {
        return new JobBuilder("composite-writer-job", jobRepository)
                .start(step())
                .build();
    }

    public static void main(String[] args) {
        SpringApplication.run(CompositeWriteJob.class, args);
    }
}
