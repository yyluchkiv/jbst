package jbst.foundation.integration.mongo.configs;

import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

public class MongoBeforeAllCallback implements BeforeAllCallback {

    @Override
    public void beforeAll(ExtensionContext context) {
        MongoContainerV506.container.start();
        this.setProperties(MongoContainerV506.container);
    }

    private void setProperties(MongoContainerV506 container) {
        System.setProperty("jbst.mongodb-security-jwt-configs.mongodb.host", container.getHost());
        System.setProperty("jbst.mongodb-security-jwt-configs.mongodb.port", String.valueOf(container.getFirstMappedPort()));
        System.setProperty("jbst.mongodb-security-jwt-configs.mongodb.database", "test");
    }

}
