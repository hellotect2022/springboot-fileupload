package com.example.fileupload.config;

import java.util.concurrent.ThreadPoolExecutor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@EnableAsync
public class AsyncConfig implements AsyncConfigurer{
	// Push Task
 	@Bean(name="myAsyncTask")
 	public TaskExecutor pushAsyncTask() {
 	   ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
 	   executor.setThreadNamePrefix("myTask-");
 	   executor.setCorePoolSize(8);	// 스레드 풀에 항상 살아있는 최소 스레드 수 (예상 최대 동시 작업 수에 가까운 값으로 설정)
 	   executor.setMaxPoolSize(100);	// 스레드 풀이 확장 할 수 있는 최대 스레드 수
 	   executor.setQueueCapacity(20);	// max pool size에 thread 사용중일경우 queue에 보관하여 처리 할 작업 갯수 ( queue size )
 	   //CorePoolSize * MaxPoolSize * 2
 	   executor.setKeepAliveSeconds(60);
 	   executor.setRejectedExecutionHandler(new ThreadPoolExecutor.DiscardOldestPolicy());
 	   return executor;
 	}
}
