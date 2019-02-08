package com.example.elasticsearchresearch.services;

import com.example.elasticsearchresearch.services.support.ActionListenerImpl;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public abstract class ElasticsearchService<T> {
  protected static final String INDEX      = "shakespeare";
  protected static final String TEXT_ENTRY = "text_entry";
  protected static final String TYPE       = "doc";
  protected static final String AREA       = "area";
  protected static final String TERMS      = "terms";
  protected static final String FIELD      = "field";
  protected static final String COUNT      = "count";

  protected RestHighLevelClient client;

  ElasticsearchService(RestHighLevelClient client) {
    this.client = client;
  }

  public CompletableFuture<T> performQuery(Optional<String> queryText) {
    return constructSearchQuery()
      .andThen(createSearchSourceBuilder())
      .andThen(createSearchRequest())
      .andThen(executeSearch())
      .andThen(mapSearchResponse())
      .apply(queryText);
  }

  protected abstract Function<CompletableFuture<SearchResponse>, CompletableFuture<T>> mapSearchResponse();


  protected Function<Optional<String>, QueryBuilder> constructSearchQuery() {
    return (textEntry) -> {
      QueryBuilder queryBuilder = textEntry
        .map(value -> (QueryBuilder) QueryBuilders.wildcardQuery(TEXT_ENTRY, value + "*"))
        .orElseGet(() -> QueryBuilders.matchAllQuery());

      return queryBuilder;
    };
  }

  protected Function<QueryBuilder, SearchSourceBuilder> createSearchSourceBuilder() {
    return (queryBuilder) -> {
      SearchSourceBuilder sourceBuilder = SearchSourceBuilder.searchSource();
      sourceBuilder.size(10000);
      sourceBuilder.query(queryBuilder);

      return sourceBuilder;
    };
  }


  protected Function<SearchSourceBuilder, SearchRequest> createSearchRequest() {
    return (sourceBuilder) -> {
      SearchRequest searchRequest = new SearchRequest(INDEX);
      searchRequest.source(sourceBuilder);
      searchRequest.types(TYPE);
      return searchRequest;
    };
  }


  protected Function<SearchRequest, CompletableFuture<SearchResponse>> executeSearch() {
    return (searchRequest) -> {
      CompletableFuture<SearchResponse> toBeCompleted = new CompletableFuture<>();

      client
        .searchAsync(
          searchRequest,
          RequestOptions.DEFAULT,
          new ActionListenerImpl<>(toBeCompleted));

      return toBeCompleted;
    };
  }
}
