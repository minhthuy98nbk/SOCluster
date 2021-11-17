package com.zingplay.kafka.api;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.listener.MessageListener;

/** @author "Bikas Katwal" 13/03/19 */
public class CustomMessageListener implements MessageListener<String, String> {

  // inject your own concrete processor
  private IMessageProcessor messageProcessor;

    public CustomMessageListener(IMessageProcessor IMessageProcessor) {
        this.messageProcessor = IMessageProcessor;
    }

    @Override
  public void onMessage(ConsumerRecord<String, String> consumerRecord) {

    // process message
    messageProcessor.process(consumerRecord.topic(), consumerRecord.key(), consumerRecord.value());
  }
}
