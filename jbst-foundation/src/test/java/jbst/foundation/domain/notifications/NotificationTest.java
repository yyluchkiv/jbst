package jbst.foundation.domain.notifications;

import jbst.foundation.domain.tests.io.TestsIOUtils;
import jbst.foundation.domain.tests.runners.AbstractObjectMapperRunner;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.function.Function;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class NotificationTest extends AbstractObjectMapperRunner {

    private static Stream<Arguments> serializeTest() {
        return Stream.of(
                Arguments.of((Function<String, Notification>) Notification::info, "notification-info.json"),
                Arguments.of((Function<String, Notification>) Notification::success, "notification-success.json"),
                Arguments.of((Function<String, Notification>) Notification::warning, "notification-warning.json"),
                Arguments.of((Function<String, Notification>) Notification::error, "notification-error.json")
        );
    }

    @ParameterizedTest
    @MethodSource("serializeTest")
    void serialize(Function<String, Notification> fnc, String fileName) {
        // Arrange
        var message = "jbst";

        // Act
        var json = this.writeValueAsString(fnc.apply(message));

        // Assert
        assertThat(json).isEqualTo(TestsIOUtils.readFile("jsons", fileName));
    }
}
