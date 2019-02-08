package com.example.elasticsearchresearch.configuration;

import com.example.elasticsearchresearch.configuration.support.ElasticSearchSetupRunner;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.AbstractApplicationContext;

@AutoConfigureAfter({ ElasticsearchConfiguration.class })
public class ElasticSearchSetupAutoConfiguration {
  @Bean
  public ElasticSearchSetupRunner runCommands(final AbstractApplicationContext applicationContext, final RestHighLevelClient client) {
    return new ElasticSearchSetupRunner(applicationContext, client);
  }
}
