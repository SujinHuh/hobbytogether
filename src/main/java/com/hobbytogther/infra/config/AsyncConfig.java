package com.hobbytogther.infra.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Slf4j
@Configuration
@EnableAsync
public class AsyncConfig implements AsyncConfigurer {
    /** 같은 Thread 에서 돌지 않게하려면 첫번째 thread는 종료하고,commit이되고, 두번째 thread가 에러를 던지더라도 영향을 주지 않을 것 */
    /** Tread 분리 , 응답시간 단축  */


    @Override
    public Executor getAsyncExecutor() {

        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        int processors = Runtime.getRuntime().availableProcessors(); //현재 프로세스 갯수
        log.info("processor count {}", processors);
        executor.setCorePoolSize(processors);//프로세서 개수만큼 설정 // 10명사
        executor.setMaxPoolSize(processors *2);//람
        executor.setQueueCapacity(50);// 줄세우는
        executor.setKeepAliveSeconds(60);//60초뒤에 정리
        executor.setThreadNamePrefix("Async Executor - ");
        executor.initialize();
        return executor;
    }
}
