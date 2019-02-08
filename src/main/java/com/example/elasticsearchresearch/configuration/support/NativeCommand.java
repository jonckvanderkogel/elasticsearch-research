package com.example.elasticsearchresearch.configuration.support;

import java.io.IOException;
import java.util.function.Consumer;

public class NativeCommand {
  public static void runCommand(String command, Consumer<Process> waitForDone) {
    try {
      Process process = Runtime.getRuntime().exec(command);

      waitForDone.accept(process);
    } catch (IOException ioe) {
      throw new RuntimeException(ioe);
    }
  }
}
