package jbst.foundation.domain.properties.configs;

import jbst.foundation.domain.base.Email;
import jbst.foundation.domain.base.Password;
import jbst.foundation.domain.base.Username;
import org.junit.jupiter.api.Test;

import static jbst.foundation.utilities.random.RandomUtility.randomIntegerGreaterThanZero;
import static jbst.foundation.utilities.random.RandomUtility.randomString;
import static org.assertj.core.api.Assertions.assertThat;

class JbstPropertyEmailsTest {

    @Test
    void constructorTest() {
        // Act
        var emailConfigs = new JbstPropertyEmails(
                true,
                randomString(),
                randomIntegerGreaterThanZero(),
                Username.random().value(),
                Username.random(),
                Password.random()
        );

        // Assert
        assertThat(emailConfigs.isEnabled()).isTrue();
        assertThat(emailConfigs.getHost()).isNotNull();
        assertThat(emailConfigs.getPort()).isNotZero();
        assertThat(emailConfigs.getFrom()).isNotNull();
        assertThat(emailConfigs.getUsername()).isNotNull();
        assertThat(emailConfigs.getPassword()).isNotNull();
    }

    @Test
    void disabledTest() {
        // Act
        var emailConfigs = JbstPropertyEmails.disabled();

        // Assert
        assertThat(emailConfigs.isEnabled()).isFalse();
        assertThat(emailConfigs.getHost()).isNull();
        assertThat(emailConfigs.getPort()).isZero();
        assertThat(emailConfigs.getFrom()).isNull();
        assertThat(emailConfigs.getUsername()).isNull();
        assertThat(emailConfigs.getPassword()).isNull();
    }

    @Test
    void enabledTest() {
        // Arrange
        var from = Email.random().value();

        // Act
        var emailConfigs = JbstPropertyEmails.enabled(from);

        // Assert
        assertThat(emailConfigs.isEnabled()).isTrue();
        assertThat(emailConfigs.getHost()).isNotNull();
        assertThat(emailConfigs.getPort()).isEqualTo(587);
        assertThat(emailConfigs.getFrom()).isEqualTo(from);
        assertThat(emailConfigs.getUsername()).isEqualTo(Username.hardcoded());
        assertThat(emailConfigs.getPassword()).isEqualTo(Password.hardcoded());
    }
}
