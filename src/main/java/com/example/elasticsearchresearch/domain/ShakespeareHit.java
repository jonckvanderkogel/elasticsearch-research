package com.example.elasticsearchresearch.domain;

public class ShakespeareHit {
  private String playName;
  private int speechNumber;
  private String lineNumber;
  private String speaker;
  private String textEntry;

  public ShakespeareHit() {}

  public ShakespeareHit(String playName, int speechNumber, String lineNumber, String speaker, String textEntry) {
    this.playName = playName;
    this.speechNumber = speechNumber;
    this.lineNumber = lineNumber;
    this.speaker = speaker;
    this.textEntry = textEntry;
  }

  public String getPlayName() {
    return playName;
  }

  public int getSpeechNumber() {
    return speechNumber;
  }

  public String getLineNumber() {
    return lineNumber;
  }

  public String getSpeaker() {
    return speaker;
  }

  public String getTextEntry() {
    return textEntry;
  }
}
