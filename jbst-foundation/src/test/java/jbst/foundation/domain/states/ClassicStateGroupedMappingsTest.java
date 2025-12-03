package jbst.foundation.domain.states;

import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ClassicStateGroupedMappingsTest {

    @Test
    void constructorNoStatesTest() {
        // Act
        var groupedMappings = new ClassicStateGroupedMappings(
                List.of()
        );

        // Assert
        assertThat(groupedMappings.getValues()).isEmpty();
        assertThat(groupedMappings.isEmpty()).isTrue();
    }

    @Test
    void constructorTest() {
        // Act
        var groupedMappings = new ClassicStateGroupedMappings(
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
