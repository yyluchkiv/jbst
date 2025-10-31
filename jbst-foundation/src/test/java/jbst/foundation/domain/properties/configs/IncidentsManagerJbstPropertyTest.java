package jbst.foundation.domain.properties.configs;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class IncidentsManagerJbstPropertyTest {

    @Test
    void disabledTest() {
        // Act
        var incidentConfigs = IncidentsManagerJbstProperty.disabled();

        // Assert
        assertThat(incidentConfigs.isEnabled()).isFalse();
        assertThat(incidentConfigs.getRemoteServer()).isNull();
    }
}
