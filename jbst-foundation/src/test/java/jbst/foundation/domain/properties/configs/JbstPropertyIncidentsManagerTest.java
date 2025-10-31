package jbst.foundation.domain.properties.configs;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class JbstPropertyIncidentsManagerTest {

    @Test
    void disabledTest() {
        // Act
        var incidentConfigs = JbstPropertyIncidentsManager.disabled();

        // Assert
        assertThat(incidentConfigs.isEnabled()).isFalse();
        assertThat(incidentConfigs.getRemoteServer()).isNull();
    }
}
