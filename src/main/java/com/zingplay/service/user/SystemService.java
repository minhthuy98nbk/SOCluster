package com.zingplay.service.user;

import com.zingplay.log.LogSystemAction;
import com.zingplay.repository.UserRepository;
import com.zingplay.zarathustra.mongo.MultiTenantMongoDbFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SystemService {
    private final UserRepository userRepository;
    private MongoTemplate template;

    private static SystemService instance;
    @Autowired
    public SystemService(UserRepository userRepository, MongoTemplate template) {
        this.userRepository = userRepository;
        this.template = template;
        instance = this;
    }

    public static SystemService getInstance() {
        return instance;
    }

    public void createIndex(){
        LogSystemAction.getInstance().info("setMongoTemplate(template)");
        //MultiTenantMongoDbFactory.setDatabaseNameForCurrentThread(defaultDBName);
        MultiTenantMongoDbFactory.setMongoTemplate(template);
        //userRepository.findById("1");
    }

}
