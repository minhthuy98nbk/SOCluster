package com.zingplay.kafka;

import com.zingplay.kafka.api.IMessageProcessor;
import com.zingplay.log.LogKafka;
import org.springframework.stereotype.Component;

@Component
public class KafkaConsumerListener implements IMessageProcessor{

    @Override
    public void process(String topic, String key, String message) {
        LogKafka.getInstance().info("Kafka|received|{}|{}|{}", topic, key, message);
        try {
            KafkaConsumerController.getInstance().process(topic,key,message);
        }catch (Exception e){
            LogKafka.getInstance().error("Kafka|received|error|{}|{}|{}", topic, key, message);
            LogKafka.getInstance().error(e.getMessage());
            e.printStackTrace();
        }
    }

}
