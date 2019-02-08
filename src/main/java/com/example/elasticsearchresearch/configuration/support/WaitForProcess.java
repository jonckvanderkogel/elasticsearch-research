package com.example.elasticsearchresearch.configuration.support;

import lombok.extern.slf4j.Slf4j;
import lombok.val;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.function.Consumer;

@Slf4j
public class WaitForProcess implements Consumer<Process> {
  public static final WaitForProcess INSTANCE = new WaitForProcess();

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

    val errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));

    String line;
    while ((line = reader.readLine()) != null) {
      log.info(line);
    }

    while ((line = errorReader.readLine()) != null) {
      log.info(line);
    }
  }
}
