package com.example._11_step_condition_decider;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.job.flow.FlowExecutionStatus;
import org.springframework.batch.core.job.flow.JobExecutionDecider;

import java.util.Random;

public class MyStatusDecider implements JobExecutionDecider {
    @Override
    public FlowExecutionStatus decide(JobExecution jobExecution, StepExecution stepExecution) {
        long ret = new Random().nextInt(3);
        if (ret == 0) {
            return new FlowExecutionStatus("A");
        } else if (ret == 1) {
            return new FlowExecutionStatus("B");
        } else {
            return new FlowExecutionStatus("C");
        }
    }
}
