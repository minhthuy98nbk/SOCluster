package com.zingplay.kafka.api;

public interface IMessageProcessor {

  void process(String topic, String key, String message);
}
