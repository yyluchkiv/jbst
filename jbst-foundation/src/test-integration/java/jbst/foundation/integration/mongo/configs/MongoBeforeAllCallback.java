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
        System.setProperty("jbst.databases.mongo.enabled", "true");
        System.setProperty("jbst.databases.mongo.database.host", container.getHost());
        System.setProperty("jbst.databases.mongo.database.port", String.valueOf(container.getFirstMappedPort()));
        System.setProperty("jbst.databases.mongo.database.name", "test");
    }

}
