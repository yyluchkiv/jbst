package jbst.foundation.domain.states;

import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class JbstStateManagerClassicTest {

    static class TestClassicStateManager extends JbstStateManagerClassic {

        @Override
        public String getLogKeyword() {
            return "[Test] {}: {} → {}";
        }

        @Override
        public String getLogId() {
            return "JUNIT";
        }
    }

    @Test
    public void print() {
        // Act
        var stateManager = new TestClassicStateManager();
        stateManager.start();
        stateManager.onActivation();
        stateManager.pause();
        stateManager.onPaused();
        stateManager.stop();
        stateManager.onTermination();
        stateManager.complete();
        stateManager.onComplete();
    }

    @Test
    void classicStateGroupedMappingsConstructorNoStatesTest() {
        // Act
        var groupedMappings = new JbstStateManagerClassic.ClassicStateGroupedMappings(
                List.of()
        );

        // Assert
        assertThat(groupedMappings.getValues()).isEmpty();
        assertThat(groupedMappings.isEmpty()).isTrue();
    }

    @Test
    void classicStateGroupedMappingsConstructorTest() {
        // Act
        var groupedMappings = new JbstStateManagerClassic.ClassicStateGroupedMappings(
                List.of(
                        JbstStateClassic.TERMINATED,
                        JbstStateClassic.CREATED,
                        JbstStateClassic.STARTING,
                        JbstStateClassic.CREATED,
                        JbstStateClassic.ACTIVE,
                        JbstStateClassic.ACTIVE,
                        JbstStateClassic.ACTIVE,
                        JbstStateClassic.ACTIVE,
                        JbstStateClassic.CREATED,
                        JbstStateClassic.COMPLETED,
                        JbstStateClassic.COMPLETING,
                        JbstStateClassic.STARTING,
                        JbstStateClassic.COMPLETED
                )
        );

        // Assert
        assertThat(groupedMappings.getValues()).hasSize(6);
        assertThat(groupedMappings.getValues()).containsExactlyEntriesOf(
                new LinkedHashMap<>() {{
                    put(JbstStateClassic.CREATED, 3L);
                    put(JbstStateClassic.STARTING, 2L);
                    put(JbstStateClassic.ACTIVE, 4L);
                    put(JbstStateClassic.TERMINATED, 1L);
                    put(JbstStateClassic.COMPLETING, 1L);
                    put(JbstStateClassic.COMPLETED, 2L);
                }}
        );
        assertThat(groupedMappings.isEmpty()).isFalse();
    }
}
