package jbst.foundation.domain.properties.configs;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class IncidentsManagerConfigsTest {

    @Test
    void disabledTest() {
        // Act
        var incidentConfigs = IncidentsManagerConfigs.disabled();

        // Assert
        assertThat(incidentConfigs.isEnabled()).isFalse();
        assertThat(incidentConfigs.getRemoteServer()).isNull();
    }
}
