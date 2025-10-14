package jbst.foundation.domain.triggers;

import jbst.foundation.domain.base.Username;
import jbst.foundation.domain.tests.runners.AbstractSerializationDeserializationRunner;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UserTriggerTest extends AbstractSerializationDeserializationRunner {
    private static final UserTrigger USER_TRIGGER = new UserTrigger(Username.hardcoded());

    @Override
    protected String getFileName() {
        return "trigger-user.json";
    }

    @Override
    protected String getFolder() {
        return "jsons";
    }

    @Test
    void serializeTest() {
        // Act
        var json = this.writeValueAsString(USER_TRIGGER);

        // Assert
        assertThat(json).isEqualTo(this.readFile());
    }
}
