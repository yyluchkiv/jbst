package jbst.foundation.domain.triggers;

import jbst.foundation.domain.tests.JbstUnitTests;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CronTriggerTest extends JbstUnitTests.Runners.BaseFolderFile {
    private static final CronTrigger CRON_TRIGGER = new CronTrigger();

    @Override
    protected String getFileName() {
        return "trigger-cron.json";
    }

    @Override
    protected String getFolder() {
        return "jsons";
    }

    @Test
    void serializeTest() {
        // Act
        var json = this.writeValueAsString(CRON_TRIGGER);

        // Assert
        assertThat(json).isEqualTo(this.readFile());
    }
}
