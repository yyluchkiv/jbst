package jbst.foundation.domain.hardware.bytes;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class JbstByteAmountTest {

    @Test
    void ofGbTest() {
        // Act
        var actual = JbstByteAmount.ofGb(1573741824L);

        // Assert
        assertThat(actual).isNotNull();
        assertThat(actual.getUnit()).isEqualTo(JbstByteUnit.GIGABYTE);
        assertThat(actual.getAmount()).isEqualTo(new BigDecimal("1.4657"));
    }

    @Test
    void ofMBTest() {
        // Act
        var actual = JbstByteAmount.ofMB(1573741824L);

        // Assert
        assertThat(actual).isNotNull();
        assertThat(actual.getUnit()).isEqualTo(JbstByteUnit.MEGABYTE);
        assertThat(actual.getAmount()).isEqualTo(new BigDecimal("1500.8"));
    }
}
