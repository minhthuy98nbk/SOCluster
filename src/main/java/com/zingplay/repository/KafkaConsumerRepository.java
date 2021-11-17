package com.zingplay.repository;

import com.zingplay.models.KafkaConsumer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface KafkaConsumerRepository extends MongoRepository<KafkaConsumer, String> {
    Page<KafkaConsumer> findByTopicContaining(String search, Pageable pageable);
    Optional<KafkaConsumer> findByTopic(String topic);

}
