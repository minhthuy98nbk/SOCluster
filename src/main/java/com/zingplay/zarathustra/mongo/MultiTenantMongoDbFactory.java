package com.zingplay.zarathustra.mongo;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoDatabase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory;
import org.springframework.data.mongodb.core.index.IndexDefinition;
import org.springframework.data.mongodb.core.index.MongoPersistentEntityIndexResolver;
import org.springframework.data.mongodb.core.mapping.BasicMongoPersistentEntity;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;
import org.springframework.data.mongodb.core.mapping.MongoPersistentEntity;

import java.util.HashMap;

public class MultiTenantMongoDbFactory extends SimpleMongoClientDatabaseFactory {

    private final String defaultName;
    private static final Logger logger = LoggerFactory.getLogger(MultiTenantMongoDbFactory.class);

    private static MongoTemplate mongoTemplate;

    private static final ThreadLocal<String> dbName = new ThreadLocal<String>();
    private static final HashMap<String, Object> databaseIndexMap = new HashMap<String, Object>();

    public MultiTenantMongoDbFactory(final MongoClient mongo, final String defaultDatabaseName) {
        super(mongo, defaultDatabaseName);
        logger.info("Instantiating " + MultiTenantMongoDbFactory.class.getName() + " with default database name: " + defaultDatabaseName);
        this.defaultName = defaultDatabaseName;
    }

    //dirty but ... what can I do?
    public static void setMongoTemplate(final MongoTemplate mongoTemplate) {
        //Assert.isNull(mongoTemplate, "You can set MongoTemplate just once");
        MultiTenantMongoDbFactory.mongoTemplate = mongoTemplate;
    }

    public static void setDatabaseNameForCurrentThread(final String databaseName) {
        logger.info("Switching to database: " + databaseName);
        dbName.set(databaseName);
    }
    public static void setDatabaseNameForCurrentThread(String game,String country) {
        setDatabaseNameForCurrentThread("dbOffer_" + game +"_" + country);
    }
    public static String getDatabaseNameForCurrentThread() {
        return dbName.get();
    }

    public static void clearDatabaseNameForCurrentThread() {
        if (logger.isDebugEnabled()) {
            logger.info("Removing database [" + dbName.get() + "]");
        }
        dbName.remove();
    }

    @Override
    protected MongoClient getMongoClient() {
        return super.getMongoClient();
    }

    @Override
    public MongoDatabase getMongoDatabase() throws DataAccessException {
        String dbToUse = getDbName();
        createIndexIfNecessaryFor(dbToUse);
        return super.getMongoDatabase(dbToUse);
    }

    private String getDbName(){
        final String tlName = MultiTenantMongoDbFactory.dbName.get();
        return (tlName != null ? tlName : this.defaultName);
    }

    private void createIndexIfNecessaryFor(final String database) {
        if (mongoTemplate == null) {
            logger.error("MongoTemplate is null, will not create any index.");
            return;
        }
        //sync and init once
        boolean needsToBeCreated = false;
        synchronized (MultiTenantMongoDbFactory.class) {
            final Object syncObj = databaseIndexMap.get(database);
            if (syncObj == null) {
                databaseIndexMap.put(database, new Object());
                needsToBeCreated = true;
            }
        }
        //make sure only one thread enters with needsToBeCreated = true
        synchronized (databaseIndexMap.get(database)) {
            if (needsToBeCreated) {
                logger.info("Creating indices for database name=[" + database + "]");
                createIndexes();
                logger.info("Done with creating indices for database name=[" + database + "]");
            }
        }
    }

    private void createIndexes() {
        final MongoMappingContext mappingContext = (MongoMappingContext) mongoTemplate.getConverter().getMappingContext();
        final MongoPersistentEntityIndexResolver indexResolver = new MongoPersistentEntityIndexResolver(mappingContext);
        for (BasicMongoPersistentEntity<?> persistentEntity : mappingContext.getPersistentEntities()) {
            checkForAndCreateIndexes(indexResolver, persistentEntity);
        }
    }

    private void checkForAndCreateIndexes(final MongoPersistentEntityIndexResolver indexResolver, final MongoPersistentEntity<?> entity) {
        //make sure its a root document
        if (entity.findAnnotation(Document.class) != null) {
            for (IndexDefinition indexDefinitionHolder : indexResolver.resolveIndexFor(entity.getType())) {
                //work because of javas reentered lock feature
                mongoTemplate.indexOps(entity.getType()).ensureIndex(indexDefinitionHolder);
            }
        }
    }
}
