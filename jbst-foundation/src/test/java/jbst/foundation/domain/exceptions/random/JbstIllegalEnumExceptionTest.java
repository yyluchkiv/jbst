package jbst.foundation.domain.exceptions.random;

import jbst.foundation.domain.tests.enums.EnumUnderTests;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class JbstIllegalEnumExceptionTest {

    @Test
    void testException() {
        // Arrange
        var clazz = EnumUnderTests.class;

        // Act
        var actual = new JbstIllegalEnumException(clazz);

        // Assert
        assertThat(actual.getMessage()).isEqualTo("Please check enum: class jbst.foundation.domain.tests.enums.EnumUnderTests");
    }
}
