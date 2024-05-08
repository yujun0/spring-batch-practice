package com.example._09_step_listener;


import org.springframework.batch.core.ChunkListener;
import org.springframework.batch.core.scope.context.ChunkContext;

public class MyChunkListener implements ChunkListener {
    @Override
    public void beforeChunk(ChunkContext context) {
        System.out.println("----------beforeStep----------");
    }

    @Override
    public void afterChunk(ChunkContext context) {
        System.out.println("----------afterChunk----------");
    }

    @Override
    public void afterChunkError(ChunkContext context) {
        // 失敗後回調
        System.out.println("----------afterChunkError----------");
    }
}
