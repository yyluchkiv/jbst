package jbst.foundation.domain.triggers;

import jbst.foundation.domain.tests.JbstUnitTests;
import jbst.foundation.domain.triggers.JbstTriggers.TriggerType;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.assertj.core.api.Assertions.assertThat;

class JbstTriggersTest extends JbstUnitTests.Runners.Base {

    @ParameterizedTest
    @EnumSource(TriggerType.class)
    void serializeTest(TriggerType state) {
        // Act
        var json = this.writeValueAsString(state).replace("\"", "");

        // Assert
        assertThat(json).isEqualTo(state.getValue());
    }
}
