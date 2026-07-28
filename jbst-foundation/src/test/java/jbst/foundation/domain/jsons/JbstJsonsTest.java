package jbst.foundation.domain.jsons;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.node.StringNode;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.math.BigDecimal;
import java.util.stream.Stream;

import static jbst.foundation.domain.constants.JbstConstants.Numbers.BigDecimals.HUNDRED;
import static jbst.foundation.domain.jsons.JbstJsons.getJsonNodeFieldValueAsBigDecimalOrZero;
import static jbst.foundation.domain.jsons.JbstJsons.getJsonNodeValueAsBigDecimalOrZero;
import static jbst.foundation.domain.random.JbstRandom.randomString;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class JbstJsonsTest {

    private static Stream<Arguments> getJsonNodeValueAsBigDecimalOrZeroTest() {
        return Stream.of(
                Arguments.of(null, BigDecimal.ZERO),
                Arguments.of(StringNode.valueOf("1.23"), new BigDecimal("1.23"))
        );
    }

    @ParameterizedTest
    @MethodSource("getJsonNodeValueAsBigDecimalOrZeroTest")
    void getJsonNodeValueAsBigDecimalOrZeroTest(JsonNode jsonNode, BigDecimal expected) {
        // Act
        var actual = getJsonNodeValueAsBigDecimalOrZero(jsonNode);

        // Assert
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void getJsonNodeFieldValueAsBigDecimalOrZeroJsonNodeNullTest() {
        // Act
        var actual = getJsonNodeFieldValueAsBigDecimalOrZero(null, randomString());

        // Assert
        assertThat(actual).isEqualTo(BigDecimal.ZERO);
    }

    @Test
    void getJsonNodeFieldValueAsBigDecimalOrZeroJsonFieldNullTest() {
        // Arrange
        var fieldName = randomString();
        var jsonNode = mock(JsonNode.class);
        when(jsonNode.get(fieldName)).thenReturn(null);

        // Act
        var actual = getJsonNodeFieldValueAsBigDecimalOrZero(jsonNode, fieldName);

        // Assert
        assertThat(actual).isEqualTo(BigDecimal.ZERO);
    }

    @Test
    void getJsonNodeFieldValueAsBigDecimalOrZeroJsonTest() {
        // Arrange
        var fieldName = randomString();
        var jsonNode = mock(JsonNode.class);
        when(jsonNode.get(fieldName)).thenReturn(StringNode.valueOf("100"));

        // Act
        var actual = getJsonNodeFieldValueAsBigDecimalOrZero(jsonNode, fieldName);

        // Assert
        assertThat(actual).isEqualTo(HUNDRED);
    }
}
