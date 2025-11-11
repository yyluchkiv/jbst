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
        var emails = new JbstPropertyEmails(
                true,
                randomString(),
                randomIntegerGreaterThanZero(),
                Username.random().value(),
                Username.random(),
                Password.random()
        );

        // Assert
        assertThat(emails.isEnabled()).isTrue();
        assertThat(emails.getHost()).isNotNull();
        assertThat(emails.getPort()).isNotZero();
        assertThat(emails.getFrom()).isNotNull();
        assertThat(emails.getUsername()).isNotNull();
        assertThat(emails.getPassword()).isNotNull();
    }

    @Test
    void disabledTest() {
        // Act
        var emails = JbstPropertyEmails.disabled();

        // Assert
        assertThat(emails.isEnabled()).isFalse();
        assertThat(emails.getHost()).isNull();
        assertThat(emails.getPort()).isZero();
        assertThat(emails.getFrom()).isNull();
        assertThat(emails.getUsername()).isNull();
        assertThat(emails.getPassword()).isNull();
    }

    @Test
    void enabledTest() {
        // Arrange
        var from = Email.random().value();

        // Act
        var emails = JbstPropertyEmails.enabled(from);

        // Assert
        assertThat(emails.isEnabled()).isTrue();
        assertThat(emails.getHost()).isNotNull();
        assertThat(emails.getPort()).isEqualTo(587);
        assertThat(emails.getFrom()).isEqualTo(from);
        assertThat(emails.getUsername()).isEqualTo(Username.hardcoded());
        assertThat(emails.getPassword()).isEqualTo(Password.hardcoded());
    }
}
