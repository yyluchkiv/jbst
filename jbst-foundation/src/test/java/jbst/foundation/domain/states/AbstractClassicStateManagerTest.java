package jbst.foundation.domain.states;

import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class AbstractClassicStateManagerTest {

    static class TestClassicStateManager extends AbstractClassicStateManager {

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
        var groupedMappings = new AbstractClassicStateManager.ClassicStateGroupedMappings(
                List.of()
        );

        // Assert
        assertThat(groupedMappings.getValues()).isEmpty();
        assertThat(groupedMappings.isEmpty()).isTrue();
    }

    @Test
    void classicStateGroupedMappingsConstructorTest() {
        // Act
        var groupedMappings = new AbstractClassicStateManager.ClassicStateGroupedMappings(
                List.of(
                        ClassicState.TERMINATED,
                        ClassicState.CREATED,
                        ClassicState.STARTING,
                        ClassicState.CREATED,
                        ClassicState.ACTIVE,
                        ClassicState.ACTIVE,
                        ClassicState.ACTIVE,
                        ClassicState.ACTIVE,
                        ClassicState.CREATED,
                        ClassicState.COMPLETED,
                        ClassicState.COMPLETING,
                        ClassicState.STARTING,
                        ClassicState.COMPLETED
                )
        );

        // Assert
        assertThat(groupedMappings.getValues()).hasSize(6);
        assertThat(groupedMappings.getValues()).containsExactlyEntriesOf(
                new LinkedHashMap<>() {{
                    put(ClassicState.CREATED, 3L);
                    put(ClassicState.STARTING, 2L);
                    put(ClassicState.ACTIVE, 4L);
                    put(ClassicState.TERMINATED, 1L);
                    put(ClassicState.COMPLETING, 1L);
                    put(ClassicState.COMPLETED, 2L);
                }}
        );
        assertThat(groupedMappings.isEmpty()).isFalse();
    }
}
