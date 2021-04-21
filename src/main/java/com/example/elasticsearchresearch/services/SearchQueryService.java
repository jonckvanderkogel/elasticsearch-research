package com.example.elasticsearchresearch.services;

import com.example.elasticsearchresearch.domain.ShakespeareHit;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class SearchQueryService extends ElasticsearchService<List<ShakespeareHit>> {
  @Autowired
  public SearchQueryService(RestHighLevelClient client) {
    super(client);
  }

  @Override
  protected Function<CompletableFuture<SearchResponse>, CompletableFuture<List<ShakespeareHit>>> mapSearchResponse() {
    return (responseFuture) -> responseFuture.thenApply((response) -> Arrays
      .stream(
        response
          .getHits()
          .getHits()
      )
      .map(hit -> convertHit(hit.getSourceAsMap()))
      .collect(Collectors.toList()));
  }

  private ShakespeareHit convertHit(Map<String, Object> sourceAsMap) {
    return new ShakespeareHit(
      (String) sourceAsMap.get("play_name"),
      (Integer) sourceAsMap.get("speech_number"),
      (String) sourceAsMap.get("line_number"),
      (String) sourceAsMap.get("speaker"),
      (String) sourceAsMap.get("text_entry")
    );
  }
}
