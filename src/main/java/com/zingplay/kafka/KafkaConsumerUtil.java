package com.zingplay.kafka;

import com.zingplay.kafka.api.CustomAckMessageListener;
import com.zingplay.kafka.api.CustomMessageListener;
import com.zingplay.log.LogKafka;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.listener.ContainerProperties;

import java.util.HashMap;
import java.util.Map;

import static org.apache.kafka.clients.consumer.ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG;

@Slf4j
public final class KafkaConsumerUtil {

    private static Map<String, ConcurrentMessageListenerContainer<String, String>> consumersMap =
            new HashMap<>();
    private static final Logger log = LoggerFactory.getLogger(LogKafka.class);

    /**
     * 1. This method first checks if consumers are already created for a given topic name 2. If the
     * consumers already exists in Map, it will just start the container and return 3. Else create a
     * new consumer and add to the Map
     *
     * @param topic              topic name for which consumers is needed
     * @param messageListener    pass implementation of MessageListener or AcknowledgingMessageListener
     *                           based on enable.auto.commit
     * @param concurrency        number of consumers you need
     * @param consumerProperties all the necessary consumer properties need to be passed in this
     */
    //@SuppressWarnings({"unchecked", "rawtypes"})
    public static void startOrCreateConsumers(
            final String topic,
            final Object messageListener,
            final int concurrency,
            final Map<String, Object> consumerProperties) {

        log.info("Consumer topic {} creating... at {}", topic,consumerProperties.get(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG));

        ConcurrentMessageListenerContainer<String, String> container = consumersMap.get(topic);
        if (container != null) {
            if (!container.isRunning()) {
                log.info("Consumer already created for topic {}, starting consumer!!", topic);
                container.start();
                log.info("Consumer for topic {} started!!!!", topic);
            }
            return;
        }

        ContainerProperties containerProps = new ContainerProperties(topic);

        containerProps.setPollTimeout(10000);
        Boolean enableAutoCommit = (Boolean) consumerProperties.get(ENABLE_AUTO_COMMIT_CONFIG);
        if (!enableAutoCommit) {
            containerProps.setAckMode(ContainerProperties.AckMode.MANUAL_IMMEDIATE);
        }

        ConsumerFactory<String, String> factory = new DefaultKafkaConsumerFactory<>(consumerProperties);
        //factory.createConsumer(consumerProperties.get(GROUP_ID_CONFIG))

        container = new ConcurrentMessageListenerContainer<>(factory, containerProps);

        if (enableAutoCommit && !(messageListener instanceof CustomMessageListener)) {
            throw new IllegalArgumentException(
                    "Expected message listener of type com.bkatwal.kafka.impl.CustomMessageListener!");
        }

        if (!enableAutoCommit && !(messageListener instanceof CustomAckMessageListener)) {
            throw new IllegalArgumentException(
                    "Expected message listener of type com.bkatwal.kafka.impl.CustomAckMessageListener!");
        }

        container.setupMessageListener(messageListener);

        if (concurrency == 0) {
            container.setConcurrency(1);
        } else {
            container.setConcurrency(concurrency);
        }

        container.start();

        consumersMap.put(topic, container);

        log.info("Consumer topic {} created and started!!", topic);
    }

    /**
     * Get the ListenerContainer from Map based on topic name and call stop on it, to stop all
     * consumers for given topic
     *
     * @param topic topic name to stop corresponding consumers
     */
    public static void stopConsumer(final String topic) {
        log.info("Consumer topic {} stopping", topic);
        ConcurrentMessageListenerContainer<String, String> container = consumersMap.get(topic);
        if(container != null){
            container.stop();
        }
        log.info("Consumer topic {} stopped!!", topic);
    }


    public static boolean isEmptyConsumer(final String topic){
        //ConcurrentMessageListenerContainer<String, String> stringStringConcurrentMessageListenerContainer = consumersMap.get(topic);
        return consumersMap.get(topic) == null;
    }


    private KafkaConsumerUtil() {
        throw new UnsupportedOperationException("Can not instantiate KafkaConsumerUtil");
    }
}
