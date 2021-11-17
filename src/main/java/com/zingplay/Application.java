package com.zingplay;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.zingplay.kafka.KafkaConsumerController;
import com.zingplay.service.user.UserService;
import com.zingplay.zarathustra.mongo.MultiTenantMongoDbFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;

@SpringBootApplication
@EnableKafka
@EnableAsync
@EnableScheduling
@EnableMongoAuditing
@Configuration
@EnableAutoConfiguration
@Component
public class Application implements ApplicationContextAware, InitializingBean {
    @Value("${spring.data.mongodb.uri}")
    private String uri;
    @Value("${spring.data.mongodb.default.database}")
    private String defaultDBName;

    private ApplicationContext context;
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.context = applicationContext;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        KafkaConsumerController.getInstance().setUserService(context.getBean(UserService.class));
    }

    @Bean
    public MongoTemplate mongoTemplate(final MongoClient mongoClient) throws Exception {
        MongoTemplate mongoTemplate = new MongoTemplate(mongoDbFactory(mongoClient));
        return mongoTemplate;
    }
    @Bean
    public MultiTenantMongoDbFactory mongoDbFactory(final MongoClient mongoClient) throws Exception {
        return new MultiTenantMongoDbFactory(mongoClient, defaultDBName);
    }

    @Bean
    public MongoClient mongoClient() {
        ConnectionString connectionString = new ConnectionString(uri);
        MongoClientSettings mongoClientSettings = MongoClientSettings.builder()
                .applyConnectionString(connectionString)
                .build();

        return MongoClients.create(mongoClientSettings);
    }
}
