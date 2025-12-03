package jbst.foundation.domain.cryptography;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static jbst.foundation.domain.cryptography.JbstEncoding.getBasicAuthenticationHeader;
import static org.assertj.core.api.Assertions.assertThat;

class JbstEncodingTest {

    private static Stream<Arguments> getBasicAuthenticationHeaderTest() {
        return Stream.of(
                Arguments.of("admin1", "Admin12!", "Basic YWRtaW4xOkFkbWluMTIh"),
                Arguments.of("admin2", "Admin22!", "Basic YWRtaW4yOkFkbWluMjIh"),
                Arguments.of("admin3", "Admin33!", "Basic YWRtaW4zOkFkbWluMzMh")
        );
    }

    @ParameterizedTest
    @MethodSource("getBasicAuthenticationHeaderTest")
    void getBasicAuthenticationHeaderTest(String username, String password, String expected) {
        // Act
        var actual = getBasicAuthenticationHeader(username, password);

        // Assert
        assertThat(actual.a()).isEqualTo("Authorization");
        assertThat(actual.b()).isEqualTo(expected);
    }
}
