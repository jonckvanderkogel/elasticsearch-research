package com.example.elasticsearchresearch.configuration.support;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.function.Consumer;
import java.util.function.Predicate;

@AllArgsConstructor
@Slf4j
public class WaitForSuccessString implements Consumer<Process> {
  private Predicate<String> doneCondition;

  @Override
  public void accept(Process process) {
    try {
      waitForSuccess(process);
      log.info("Successfully finished process");
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private void waitForSuccess(
    final Process process
  ) throws IOException {

    val reader =
      new BufferedReader(new InputStreamReader(process.getInputStream()));

    String line;
    while ((line = reader.readLine()) != null) {
      if (doneCondition.test(line)) {
        break;
      } else {
        log.info(line);
      }
    }
  }
}
