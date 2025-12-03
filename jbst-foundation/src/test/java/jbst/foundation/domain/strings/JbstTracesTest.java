package jbst.foundation.domain.strings;

import org.junit.jupiter.api.RepeatedTest;

import static jbst.foundation.domain.strings.JbstTraces.getTrace;
import static org.assertj.core.api.Assertions.assertThat;

class JbstTracesTest {

    @RepeatedTest(10)
    void getTraceTest() {
        // Arrange
        var npe = new NullPointerException("jbst");

        // Act
        var actual = getTrace(npe);

        // Arrange
        assertThat(actual).isNotNull();
        assertThat(actual.value()).isNotNull();
        assertThat(actual.value().length()).isGreaterThan(10000);
        assertThat(actual.value()).startsWith("java.lang.NullPointerException: jbst");
        assertThat(actual.value()).contains("at jbst.foundation.domain.strings.JbstTracesTest.getTraceTest");
    }
}
