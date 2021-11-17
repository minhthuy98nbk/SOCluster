package com.zingplay.kafka;

import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.config.SslConfigs;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.ProducerListener;
import org.springframework.kafka.support.converter.StringJsonMessageConverter;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConfiguration {
    private final Logger LOG = LoggerFactory.getLogger(getClass());

    @Value("${spring.kafka.producer.bootstrap-servers}")
    private String bootstrapServers;
    @Value("${spring.kafka.topic.default}")
    private String topicDefault;
    @Value("${spring.kafka.producer.acks}")
    private String acks;
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


    @Bean
    Map<String, Object> producerConfigs() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG, requestTimeout);
        props.put(ProducerConfig.ACKS_CONFIG, acks);
        props.put(ProducerConfig.RECONNECT_BACKOFF_MS_CONFIG, 10000);
        props.put(ProducerConfig.RECONNECT_BACKOFF_MAX_MS_CONFIG, 3);
        if(isEnableSSL) {
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

    @Bean
    ProducerFactory<String, String> producerFactory() {
        return new DefaultKafkaProducerFactory<>(producerConfigs());
    }

    @Bean
    KafkaTemplate<String, String> kafkaTemplate() {
        KafkaTemplate<String, String> kafkaTemplate = new KafkaTemplate<>(producerFactory());
        kafkaTemplate.setMessageConverter(new StringJsonMessageConverter());
        kafkaTemplate.setDefaultTopic(topicDefault);
        kafkaTemplate.setProducerListener(new ProducerListener<String, String>() {
            @Override
            public void onSuccess(ProducerRecord<String, String> producerRecord, RecordMetadata recordMetadata) {
                LOG.info("Receive Ack [{}]topic[{}] key[{}] value[{}]",recordMetadata.offset(),producerRecord.topic(), producerRecord.key(), producerRecord.value());
                //Receive Ack [7]
                // topic[so_dev_dev]
                // key[offer]
                // value[{"version":"1.0","userId":"11",
                // "offers":[{"id":"602111b94184e61b10b50420","name":"displayName1","idRunOffer":"RunOffer1","priority":1,"basePrice":2000,"price":100,"bonus":59,"iconNum":1,
                // "timeStart":"Jan 15, 2021 11:28:20 PM","timeEnd":"Feb 18, 2021 9:43:11 AM",
                // "items":[{"id":"gold","value":123456},{"id":"vpoint","value":100},{"id":"outgame","value":100}]}]}]
                KafkaConsumerController.getInstance().onReceiveAck(producerRecord);
            }

            @Override
            public void onError(ProducerRecord<String, String> producerRecord, Exception exception) {
                System.out.println("...... error ack " + producerRecord + " | " + exception);
            }
        });
        return kafkaTemplate;
    }

    //@Bean
    //public NewTopic topic(){
    //    return new NewTopic(KafkaConsumerController.BACK_END_TOPIC,1, (short) 1);
    //}
    //@Bean
    //public NewTopic topic1(){
    //    return new NewTopic(KafkaConsumerController.BACK_END_TOPIC_TEST,1, (short) 1);
    //}
    @Bean
    KafkaAdmin admin(){
        KafkaAdmin kafkaAdmin = new KafkaAdmin(producerConfigs());
        kafkaAdmin.setAutoCreate(true);
        return kafkaAdmin;
    }
    @Bean
    AdminClient adminClient(){
        return AdminClient.create(producerConfigs());
    }
}
