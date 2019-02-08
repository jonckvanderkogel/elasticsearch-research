package com.example.elasticsearchresearch.services.support;

import org.elasticsearch.action.ActionListener;

import java.util.concurrent.CompletableFuture;

public class ActionListenerImpl <T> implements ActionListener<T> {
  private CompletableFuture<T> toBeCompleted;

  public ActionListenerImpl(CompletableFuture<T> toBeCompleted) {
    this.toBeCompleted = toBeCompleted;
  }

  @Override
  public void onResponse(T response) {
    toBeCompleted.complete(response);
  }

  @Override
  public void onFailure(Exception e) {
    toBeCompleted.completeExceptionally(e);
  }
}
