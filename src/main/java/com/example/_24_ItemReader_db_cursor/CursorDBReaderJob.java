package com.example._24_ItemReader_db_cursor;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.ArgumentPreparedStatementSetter;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

// 讀取資料庫資料封裝 user 並打印
@SpringBootApplication
public class CursorDBReaderJob {
    @Autowired
    private JobRepository jobRepository;
    @Autowired
    private PlatformTransactionManager transactionManager;
    @Autowired
    private DataSource dataSource;

    @Bean
    public ItemWriter<User> itemWriter() {
        return new ItemWriter<User>() {
            @Override
            public void write(Chunk<? extends User> chunk) throws Exception {
                chunk.forEach(System.err::println);
            }
        };
    }

    // 將 row 與 object 屬性一一映射
    @Bean
    public UserRowMapper userRowMapper() {
        return new UserRowMapper();
    }

    // 使用 JDBC cursor 讀資料
    @Bean
    public JdbcCursorItemReader<User> userItemReader() {
        return new JdbcCursorItemReaderBuilder<User>()
                .name("userItemReader")
                // 連接資料庫，Spring 容器自己實現
                .dataSource(dataSource)
                // 執行 sql 查詢資料，將返回資料以 cursor 形式一條一條讀
                .sql("SELECT * FROM user WHERE age > ?")
                .preparedStatementSetter(new ArgumentPreparedStatementSetter(new Object[]{16}))
                // 資料庫讀出的資料與用戶Object屬性一條一條讀
                .rowMapper(userRowMapper())
                .build();
    }

    @Bean
    public Step step() {
        return new StepBuilder("step1", jobRepository)
                .<User, User>chunk(1, transactionManager)   // 一次讀多少數據
                .reader(userItemReader())
                .writer(itemWriter())
                .build();
    }

    @Bean
    public Job job() {
        return new JobBuilder("cursor-db-reader-job1", jobRepository)
                .start(step())
                .build();
    }

    public static void main(String[] args) {
        SpringApplication.run(CursorDBReaderJob.class, args);
    }
}
