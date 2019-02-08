package com.example.elasticsearchresearch.services;

import com.example.elasticsearchresearch.services.support.Area;
import com.example.elasticsearchresearch.services.support.ArrayNodeCollector;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.BucketOrder;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

@Service
public class TermsQueryService extends ElasticsearchService<ArrayNode> {
  private static final ObjectMapper MAPPER = new ObjectMapper();

  @Autowired
  public TermsQueryService(RestHighLevelClient client) {
    super(client);
  }

  @Override
  protected Function<QueryBuilder, SearchSourceBuilder> createSearchSourceBuilder() {
    return super.createSearchSourceBuilder()
      .andThen(addAggregations());
  }

  private Function<SearchSourceBuilder, SearchSourceBuilder> addAggregations() {
    return (sourceBuilder) -> {
      Arrays.stream(Area.values())
        .forEach(area -> {
          sourceBuilder
            .aggregation(
              AggregationBuilders
                .terms(area.getDescription())
                .field(area.getPath())
                .order(BucketOrder.count(false))
            );
        });

      return sourceBuilder;
    };
  }

  @Override
  protected Function<CompletableFuture<SearchResponse>, CompletableFuture<ArrayNode>> mapSearchResponse() {
    return (responseFuture) -> responseFuture
      .thenApply(response -> {
                   return Arrays.stream(Area.values())
                     .map(
                       area ->
                         createBucketNode(
                           area.getDescription(),
                           response.getAggregations()
                         )
                     )
                     .collect(new ArrayNodeCollector());
                 }
      );
  }

  private ObjectNode createBucketNode(String area, Aggregations aggregations) {
    final ObjectNode bucketNode = MAPPER.createObjectNode();

    Terms terms = aggregations.get(area);

    List<? extends Terms.Bucket> bucketList = terms.getBuckets();

    ArrayNode arrayNode = bucketList.stream()
      .map(
        bucket ->
          createTermsNode(
            bucket.getKeyAsString(),
            bucket.getDocCount()
          )
      )
      .collect(new ArrayNodeCollector());

    bucketNode.put(AREA, area);
    bucketNode.set(TERMS, arrayNode);

    return bucketNode;
  }

  private ObjectNode createTermsNode(String term, Long count) {
    ObjectNode termNode = MAPPER.createObjectNode();

    termNode.put(FIELD, term);
    termNode.put(COUNT, count);

    return termNode;
  }
}
