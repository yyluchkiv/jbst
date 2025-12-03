package jbst.foundation.domain.collections;

import jbst.foundation.domain.base.Username;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static jbst.foundation.domain.collections.JbstCollectors.toSingleton;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.catchThrowable;

@Slf4j
class JbstCollectorsTest {

    @Test
    void toSingletonExceptionTest() {
        // Arrange
        var list = List.of(1, 2, 3, 4);

        // Act
        var throwable = catchThrowable(() -> {
            var actual = list.stream()
                    .filter(element -> element % 2 == 0)
                    .collect(toSingleton());
            LOGGER.debug("Not reachable. Actual: {}", actual);
        });

        // Assert
        assertThat(throwable).isNotNull();
        assertThat(throwable.getMessage()).isEqualTo("Collector toSingleton() expects one element");
    }

    @Test
    void toSingletonTest() {
        // Arrange
        var list = List.of(1, 2, 3, 4);

        // Act
        var actual = list.stream()
                .filter(element -> element == 4)
                .collect(toSingleton());

        // Assert
        assertThat(actual).isEqualTo(4);
    }

    @Test
    void toConcurrentKeySetTest() {
        // Arrange
        var usernames = Set.of(
                Username.of("user1"),
                Username.of("user2"),
                Username.of("user3"),
                Username.of("user4"),
                Username.of("user5")
        );

        // Act
        var actual = usernames.stream().collect(JbstCollectors.toConcurrentKeySet());

        // Assert
        assertThat(actual)
                .hasSize(usernames.size())
                .containsAll(usernames)
                .isExactlyInstanceOf(ConcurrentHashMap.KeySetView.class);
    }
}
