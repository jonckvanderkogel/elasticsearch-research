package com.example.elasticsearchresearch.configuration;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.UnknownHostException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

@Configuration
public class ElasticsearchConfiguration {

  @Bean(destroyMethod = "close")
  public RestHighLevelClient client() {

    return new RestHighLevelClient(
      RestClient.builder(
        new HttpHost("localhost", 9200, "http")));
  }

  @Bean(name = "elasticSearchExecutor")
  public Executor elasticSearchExecutor() {
    ThreadFactory threadFactory = new ThreadFactoryBuilder()
      .setNameFormat("elasticSearchExecutor-%d")
      .setDaemon(false)
      .build();
    ExecutorService executorService = Executors.newFixedThreadPool(10, threadFactory);

    return executorService;
  }
}
