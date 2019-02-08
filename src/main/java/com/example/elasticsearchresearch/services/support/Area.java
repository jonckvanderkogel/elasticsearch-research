package com.example.elasticsearchresearch.services.support;

public enum Area {
  PLAY("Play", "play_name.keyword"),
  SPEECH_NUMBER("Speech Number", "speech_number.keyword"),
  SPEAKER("Speaker", "speaker.keyword");

  private String description;
  private String path;

  private Area(String description, String path) {
    this.description = description;
    this.path = path;
  }

  public String getDescription() {
    return this.description;
  }

  public String getPath() {
    return this.path;
  }
}
