package com.zingplay.kafka;

import com.mongodb.client.MongoClient;
import com.zingplay.log.LogSystemAction;
import com.zingplay.repository.KafkaConsumerRepository;
import com.zingplay.service.user.SystemService;
import com.zingplay.socket.SocketConst;
import com.zingplay.zarathustra.mongo.MultiTenantMongoDbFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class KafkaConsumerAutoStart {
    final KafkaConsumerRepository kafkaConsumerRepository;
    final KafkaService kafkaService;
    private MongoClient mongoClient;

    private static final Logger log = LoggerFactory.getLogger(KafkaConsumerUtil.class);

    public static KafkaConsumerAutoStart instance;
    @Autowired
    public KafkaConsumerAutoStart(KafkaConsumerRepository kafkaConsumerRepository, KafkaService kafkaService, MongoClient mongoClient) {
        this.kafkaConsumerRepository = kafkaConsumerRepository;
        this.kafkaService = kafkaService;
        this.mongoClient = mongoClient;
        instance = this;
    }

    public static KafkaConsumerAutoStart getInstance() {
        return instance;
    }

    @EventListener
    public void startAuto(ApplicationReadyEvent event) throws InterruptedException {
        //add all consumer run
        log.info("KafkaConsumer register consumer topic...");
        SystemService.getInstance().createIndex();
        mongoClient.listDatabaseNames().forEach(s -> {
            if(!s.startsWith("dbOffer_")) return;
            LogSystemAction.getInstance().info("startAuto kafka consumer|" + s);
            String[] split = s.split("_");
            if(split.length < 3) return;
            String game = split[1];
            String country = split[2];
            MultiTenantMongoDbFactory.setDatabaseNameForCurrentThread(game,country);
            kafkaConsumerRepository.findAll().forEach(kafkaConsumer -> {
                int status = kafkaConsumer.getStatus();
                KafkaConsumerStatus[] values = KafkaConsumerStatus.values();
                if(status < values.length){
                    KafkaConsumerStatus kafkaConsumerStatus = values[status];
                    switch (kafkaConsumerStatus){
                        case STOPPED:
                            //nothign
                            LogSystemAction.getInstance().info("startConsumer|stopped|" + kafkaConsumer.getTopic());
                            kafkaService.stopConsumer(kafkaConsumer.getTopic());
                            break;
                        case STARTED:
                        case DISCONNECTED:
                            kafkaService.startConsumer(kafkaConsumer.getTopic());
                            break;
                    }
                }
            });
        });

        kafkaService.startConsumer(SocketConst.BACK_END_TOPIC_TEST);
        kafkaService.startConsumer(SocketConst.BACK_END_TOPIC);
        // TopicBuilder.name("p2_action_so_request").build();
        // Thread.sleep(5000);
        // log.info("---------------------------------");
        // kafkaService.sendMessage("system_offer_game_country_test","game_country_consumer", "offer");
        KafkaSendingWorker.getInstance().start();
        //MultiTenantMongoDbFactory.setDatabaseNameForCurrentThread("p2_ph");
        //KafkaSendingWorker.getInstance().send(KafkaConsumerController.BACK_END_TOPIC,"dev|dev|user_request_offers","1|123|1617609323704");
    }
}
