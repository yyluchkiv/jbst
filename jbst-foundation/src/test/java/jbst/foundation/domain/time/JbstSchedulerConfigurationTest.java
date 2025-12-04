package jbst.foundation.domain.time;

import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;

import static jbst.foundation.domain.random.JbstRandom.randomEnum;
import static jbst.foundation.domain.random.JbstRandom.randomLongGreaterThanZero;
import static org.assertj.core.api.Assertions.assertThat;

class JbstSchedulerConfigurationTest {

    @Test
    void neverTest() {
        // Act
        var sc = JbstSchedulerConfiguration.never();

        // Assert
        assertThat(sc).isNotNull();
        assertThat(sc.initialDelay()).isEqualTo(9223372036854775807L);
        assertThat(sc.delay()).isEqualTo(9223372036854775807L);
        assertThat(sc.unit()).isEqualTo(TimeUnit.DAYS);
        assertThat(sc.unit().toSeconds(sc.delay())).isEqualTo(9223372036854775807L);
    }

    @Test
    void constructorTest() {
        // Arrange
        var initialDelay = randomLongGreaterThanZero();
        var delay = randomLongGreaterThanZero();
        var unit = randomEnum(TimeUnit.class);

        // Act
        var schedulerConfiguration = new JbstSchedulerConfiguration(initialDelay, delay, unit);

        // Assert
        assertThat(schedulerConfiguration).isNotNull();
        assertThat(schedulerConfiguration.initialDelay()).isEqualTo(initialDelay);
        assertThat(schedulerConfiguration.delay()).isEqualTo(delay);
        assertThat(schedulerConfiguration.unit()).isEqualTo(unit);
    }

    @RepeatedTest(100)
    void getDeviatedSchedulerConfiguration1PercentTest() {
        // Act
        var schedulerConfiguration = JbstSchedulerConfiguration.EVERY_1_HOUR.getDeviatedSchedulerConfiguration(1);

        // Assert
        assertThat(schedulerConfiguration.unit()).isEqualTo(TimeUnit.SECONDS);
        assertThat(schedulerConfiguration.initialDelay()).isGreaterThanOrEqualTo(3564);
        assertThat(schedulerConfiguration.initialDelay()).isLessThanOrEqualTo(3636);
        assertThat(schedulerConfiguration.delay()).isGreaterThanOrEqualTo(3564);
        assertThat(schedulerConfiguration.delay()).isLessThanOrEqualTo(3636);
    }

    @RepeatedTest(100)
    void getDeviatedSchedulerConfiguration3PercentTest() {
        // Act
        var schedulerConfiguration = JbstSchedulerConfiguration.EVERY_1_HOUR.getDeviatedSchedulerConfiguration(3);

        // Assert
        assertThat(schedulerConfiguration.unit()).isEqualTo(TimeUnit.SECONDS);
        assertThat(schedulerConfiguration.initialDelay()).isGreaterThanOrEqualTo(3492);
        assertThat(schedulerConfiguration.initialDelay()).isLessThanOrEqualTo(3708);
        assertThat(schedulerConfiguration.delay()).isGreaterThanOrEqualTo(3492);
        assertThat(schedulerConfiguration.delay()).isLessThanOrEqualTo(3708);
    }
}
