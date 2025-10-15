package jbst.foundation.integration.mongo.configs;

import org.testcontainers.containers.MongoDBContainer;

@SuppressWarnings("resource")
public class MongoContainerV506 extends MongoDBContainer {
    public static final String MONGO_DB_VERSION = "mongo:5.0.6";
    public static final int MONGO_DB_PORT = 27017;

    public static MongoContainerV506 container = (MongoContainerV506) new MongoContainerV506()
            .withExposedPorts(MONGO_DB_PORT)
            .withReuse(true);

    private MongoContainerV506() {
        super(MONGO_DB_VERSION);
    }

}
