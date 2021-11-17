package com.zingplay.kafka.api;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.listener.AcknowledgingMessageListener;
import org.springframework.kafka.support.Acknowledgment;

/** @author "Bikas Katwal" 13/03/19 */
public class CustomAckMessageListener implements AcknowledgingMessageListener<String, String> {

  // inject your own concrete processor
  private IMessageProcessor messageProcessor;

    public CustomAckMessageListener(IMessageProcessor messageProcessor) {
        this.messageProcessor = messageProcessor;
    }

    @Override
  public void onMessage(
      ConsumerRecord<String, String> consumerRecord, Acknowledgment acknowledgment) {

    // process message
    messageProcessor.process(consumerRecord.topic(), consumerRecord.key(), consumerRecord.value());

    // commit offset
    acknowledgment.acknowledge();
  }
}
