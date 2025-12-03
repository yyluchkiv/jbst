package jbst.foundation.domain.collections;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.RepeatedTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.catchThrowable;

@Slf4j
class JbstPartitionsTest {

    @RepeatedTest(10)
    void integrationTest() {
        // Arrange
        var values = List.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);

        // Act
        var actual = JbstPartitions.ofSize(values, 3);

        // Assert
        assertThat(actual).isNotEqualTo(values);
        assertThat(actual.hashCode()).isPositive();
        assertThat(actual).hasSize(4);
        assertThat(actual.get(0)).hasSize(3);
        assertThat(actual.get(0)).containsExactly(1, 2, 3);
        assertThat(actual.get(1)).hasSize(3);
        assertThat(actual.get(1)).containsExactly(4, 5, 6);
        assertThat(actual.get(2)).hasSize(3);
        assertThat(actual.get(2)).containsExactly(7, 8, 9);
        assertThat(actual.get(3)).hasSize(1);
        assertThat(actual.get(3)).containsExactly(10);

        var throwable = catchThrowable(() -> {
            var partition = actual.get(-1);
            LOGGER.debug("Not reachable. Partition (-1): {}", partition);
        });

        assertThat(throwable).isInstanceOf(IndexOutOfBoundsException.class);
    }
}
