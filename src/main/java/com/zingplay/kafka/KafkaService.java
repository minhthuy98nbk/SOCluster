package com.zingplay.kafka;

import com.zingplay.kafka.api.CustomMessageListener;
import com.zingplay.log.LogKafka;
import com.zingplay.log.LogSystemAction;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.config.SslConfigs;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class KafkaService {

    private static KafkaService instance;
    @Value("${spring.kafka.consumer.group-id}")
    private String groupId;

    @Value("${spring.kafka.consumer.bootstrap-servers}")
    private String bootstrapServers;
    @Value("${spring.kafka.auto.offset.reset}")
    private String autoResetOffset;
    @Value("${spring.kafka.request.timeout.ms}")
    private String requestTimeout;
    @Value("${spring.kafka.sll.enable}")
    private boolean isEnableSSL;
    @Value("${spring.kafka.sll.truststore.localtion}")
    private String trustStorePath;
    @Value("${spring.kafka.sll.truststore.password}")
    private String trustStorePassword;
    @Value("${spring.kafka.sll.keystore.localtion}")
    private String keystorePath;
    @Value("${spring.kafka.sll.keystore.password}")
    private String keystorePassword;
    @Value("${spring.kafka.sll.key.password}")
    private String keyPassword;
    @Value("${spring.kafka.sll.endpoint.identification}")
    private String endpointIdentification;
    @Value("${spring.kafka.topic.replicas:1}")
    private int replicas;

    @Autowired
    KafkaConfiguration kafkaConfiguration;


    public static KafkaService getInstance(){
        return instance;
    }
    @Autowired
    public KafkaService(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
        instance = this;
    }

    private KafkaTemplate<String, String> kafkaTemplate;


    private Map<String, Object> producerConfigs() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 500);
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, true);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, autoResetOffset);
        props.put(ConsumerConfig.REQUEST_TIMEOUT_MS_CONFIG, requestTimeout);
        props.put(ConsumerConfig.RECONNECT_BACKOFF_MS_CONFIG, 10000);
        props.put(ConsumerConfig.RECONNECT_BACKOFF_MAX_MS_CONFIG, 1000);
        props.put(ConsumerConfig.ALLOW_AUTO_CREATE_TOPICS_CONFIG, true);
        if (isEnableSSL) {
            props.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, "SSL");
            props.put(SslConfigs.SSL_TRUSTSTORE_LOCATION_CONFIG, trustStorePath);
            props.put(SslConfigs.SSL_TRUSTSTORE_PASSWORD_CONFIG, trustStorePassword);
            props.put(SslConfigs.SSL_KEYSTORE_LOCATION_CONFIG, keystorePath);
            props.put(SslConfigs.SSL_KEYSTORE_PASSWORD_CONFIG, keystorePassword);
            props.put(SslConfigs.SSL_KEY_PASSWORD_CONFIG, keyPassword);
            props.put(SslConfigs.SSL_ENDPOINT_IDENTIFICATION_ALGORITHM_CONFIG, endpointIdentification);
        }
        return props;
    }


    public void startConsumer(final String topic) {
        LogSystemAction.getInstance().info("startConsumer|started|" + topic);
        kafkaConfiguration.adminClient().createTopics(Stream.of(topic).map(s -> new NewTopic(s,1, (short) replicas)).collect(Collectors.toList()));
        //TopicBuilder.name(topic).replicas(replicas).build();
        //new NewTopic(topic,1, (short) replicas);
        KafkaConsumerUtil.startOrCreateConsumers(topic, new CustomMessageListener(new KafkaConsumerListener()), 2, producerConfigs());
        //boolean emptyConsumer = KafkaConsumerUtil.isEmptyConsumer(topic);
        //if(emptyConsumer){
        //
        //}
    }

    public void stopConsumer(final String topic) {
        LogSystemAction.getInstance().info("startConsumer|stopped|" + topic);
        kafkaConfiguration.adminClient().deleteTopics(Stream.of(topic).collect(Collectors.toList()));
        KafkaConsumerUtil.stopConsumer(topic);
    }
    public void stopConsumer(final String topic, boolean removeTopic) {
        LogSystemAction.getInstance().info("startConsumer|stopped|removed|" + topic);
        kafkaConfiguration.adminClient().deleteConsumerGroups(Stream.of(topic).collect(Collectors.toList()));
        KafkaConsumerUtil.stopConsumer(topic);
    }

    public void sendMessage(final String topic, final String key, final String message) {
        LogKafka.getInstance().info("Kafka|send|{}|{}|{}", topic, key, message);
        //TopicBuilder.name(topic).replicas(replicas).build();
        kafkaTemplate.send(new ProducerRecord<>(topic, null, key, message));
    }
}
