package com.example.elasticsearchresearch.controllers;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.example.elasticsearchresearch.domain.ShakespeareHit;
import com.example.elasticsearchresearch.services.SearchQueryService;
import com.example.elasticsearchresearch.services.TermsQueryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletionStage;

@RestController
@RequestMapping("/search")
public class ElasticsearchController {
  @Autowired
  private SearchQueryService searchQueryService;

  @Autowired
  private TermsQueryService termsQueryService;

  @RequestMapping(method = RequestMethod.GET)
  public CompletionStage<ResponseEntity<List<ShakespeareHit>>> searchTextEntry(@RequestParam("query") Optional<String> query) {
    return searchQueryService
      .performQuery(query)
      .thenApply(this::buildResponse);
  }

  @RequestMapping(method = RequestMethod.GET, path = "/terms")
  public CompletionStage<ResponseEntity<ArrayNode>> termsQuery(@RequestParam("query") Optional<String> query) {
    return termsQueryService
      .performQuery(query)
      .thenApply(this::buildResponse);
  }

  private <T> ResponseEntity<T> buildResponse(T response) {
      return ResponseEntity.ok(response);
  }
}
