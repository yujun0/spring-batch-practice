package com.example._16_job_stop;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;

public class StopStepListener implements StepExecutionListener {
    @Override
    public void beforeStep(StepExecution stepExecution) {
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        // 不滿足條件處理
        if (ResourceCount.readCount != ResourceCount.totalCount) {
            return ExitStatus.STOPPED;
        }
        return stepExecution.getExitStatus();
    }
}
