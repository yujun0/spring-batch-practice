package com.example._15_job_start_restful;

import org.springframework.batch.core.*;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {
    @Autowired
    private JobLauncher jobLauncher;
    @Autowired
    private Job job;
    @Autowired
    private JobExplorer jobExplorer;

    // http://localhost:8080/job/start
    @GetMapping("/job/start")
    public ExitStatus startJob(String id) throws Exception {
        // run.id 自增前提：須先獲取到之前 jobParameter 中的 run.id 才能進行自增
        JobParameters parameters = new JobParametersBuilder(jobExplorer)
                .getNextJobParameters(job)
                .addString("id", id).toJobParameters();

        JobExecution jobExecution = jobLauncher.run(job, parameters);
        // 啟動 job 作業
        return jobExecution.getExitStatus();
    }
}
