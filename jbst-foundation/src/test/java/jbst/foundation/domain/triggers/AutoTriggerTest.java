package jbst.foundation.domain.triggers;

import jbst.foundation.domain.base.Username;
import jbst.foundation.domain.tests.runners.AbstractSerializationDeserializationRunner;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AutoTriggerTest extends AbstractSerializationDeserializationRunner {
    private static final AutoTrigger AUTO_TRIGGER = new AutoTrigger(Username.ops());

    @Override
    protected String getFileName() {
        return "trigger-auto.json";
    }

    @Override
    protected String getFolder() {
        return "jsons";
    }

    @Test
    void serializeTest() {
        // Act
        var json = this.writeValueAsString(AUTO_TRIGGER);

        // Assert
        assertThat(json).isEqualTo(this.readFile());
    }
}
