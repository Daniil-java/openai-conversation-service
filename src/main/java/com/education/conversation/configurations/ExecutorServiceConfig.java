package com.education.conversation.configurations;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class ExecutorServiceConfig {
    private static final int THREAD_POOL_COUNT = 5;

    @Bean
    public ExecutorService taskExecutor() {
        return Executors.newFixedThreadPool(THREAD_POOL_COUNT);
    }
}
