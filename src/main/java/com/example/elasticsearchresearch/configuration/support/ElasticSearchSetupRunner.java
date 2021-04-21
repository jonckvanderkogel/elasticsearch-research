package com.example.elasticsearchresearch.configuration.support;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.Request;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.context.support.AbstractApplicationContext;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.StreamSupport;

import static com.example.elasticsearchresearch.configuration.support.NativeCommand.runCommand;

@Slf4j
@AllArgsConstructor
public class ElasticSearchSetupRunner {
  private static final String INDEX = "shakespeare";
  private final AbstractApplicationContext applicationContext;
  private final RestHighLevelClient        client;

  @PostConstruct
  public void runCommands() throws IOException {
    Consumer<Process> consumer = new WaitForSuccessString(
      (line) -> line.endsWith("mode [basic] - valid")
    );

    runCommand("docker run -p 9200:9200 -p 9300:9300 -e \"discovery.type=single-node\" docker.elastic.co/elasticsearch/elasticsearch:6.6.0",
               consumer);

    createIndex();

    loadJson();

    applicationContext.registerShutdownHook();
  }

  private void createIndex() throws IOException {
    StringEntity entity = new StringEntity("{\"mappings\": {\"doc\": {\"properties\": {\"speaker\": {\"type\": \"text\", \"fields\": {\"keyword\":{\"type\": \"keyword\"}}}, \"play_name\": {\"type\": \"text\", \"fields\": {\"keyword\":{\"type\": \"keyword\"}}}, \"line_id\": {\"type\": \"integer\"}, \"speech_number\": {\"type\": \"text\", \"fields\": {\"keyword\":{\"type\": \"keyword\"}}}}}}}", ContentType.APPLICATION_JSON);
    Request request = new Request("PUT", "/" + INDEX);
    request.setEntity(entity);

    client.getLowLevelClient().performRequest(request);
  }

  private void loadJson() throws IOException {
    BulkRequest request = new BulkRequest();
    int count = 0;
    int batch = 15000;

    BufferedReader br = new BufferedReader(new FileReader("shakespeare_6.0.json"));

    String line;

    while ((line = br.readLine()) != null) {
      if (line.startsWith("{\"type\":\"line\"")) {
        request.add(new IndexRequest(INDEX, "doc").source(line, XContentType.JSON));
      }
      count++;
      if (count % batch == 0) {
        processBulkRequest()
          .apply(request)
          .ifPresent(this::logBulkErrors);
        log.info("Uploaded {} so far", count);
        request = new BulkRequest();
      }
    }

    if (request.numberOfActions() > 0) {
      processBulkRequest()
        .apply(request)
        .ifPresent(this::logBulkErrors);
    }

    log.info("Total uploaded: " + count);
  }

  private Function<BulkRequest, Optional<BulkResponse>> processBulkRequest() {
    return (bulkRequest) -> {
      try {
        BulkResponse bulkResponse = client
          .bulk(
            bulkRequest,
            RequestOptions.DEFAULT
          );
        return bulkResponse.hasFailures() ? Optional.of(bulkResponse) : Optional.empty();
      } catch (IOException ioe) {
        throw new RuntimeException(ioe);
      }
    };
  }

  private void logBulkErrors(BulkResponse bulkResponse) {
    StreamSupport.stream(bulkResponse.spliterator(), false)
      .filter(BulkItemResponse::isFailed)
      .forEach(item -> log.error("Error {}", item.getFailure().toString()));
  }
}
