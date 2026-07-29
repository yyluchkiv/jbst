package jbst.foundation.domain.properties;

import jbst.foundation.configurations.TestJbstConfigurationPropertiesFixed;
import org.junit.jupiter.api.Test;
import org.springframework.boot.web.server.autoconfigure.ServerProperties;

import java.lang.reflect.InvocationTargetException;

import static jbst.foundation.domain.reflection.JbstReflections.getGetters;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class JbstPropertiesTest {

    private final JbstProperties jbstProperties = new TestJbstConfigurationPropertiesFixed().jbstProperties();

    @Test
    void jbstPropertiesTest() {
        // Act
        var getters = getGetters(this.jbstProperties);

        // Assert
        assertThat(getters).hasSize(11);
        getters.forEach(getter -> {
            try {
                var propertiesConfigs = getter.invoke(this.jbstProperties);
                assertThat(propertiesConfigs).isNotNull();
            } catch (IllegalAccessException | InvocationTargetException ex) {
                throw new RuntimeException(ex);
            }
        });
    }

    @Test
    void getMagicLink() {
        // Act
        var token = "9F2FCF5EFC2A026B319D5D267C06D8D06B0C18C1";
        var link = this.jbstProperties.getMagicLink(
                token
        );

        // Assert
        assertThat(link).isEqualTo("http://127.0.0.1:3000/magic-link?token=" + token);
    }

    @Test
    void getEmailConfirmationRedirectLink() {
        // Act
        var link = this.jbstProperties.getEmailConfirmationRedirectLink();

        // Assert
        assertThat(link).isEqualTo("http://127.0.0.1:3000/email-confirmation");
    }

    @Test
    void getEmailConfirmationLink() {
        // Act
        var contextPath = "/tests-context-path";
        var servlet = mock(ServerProperties.Servlet.class);
        var serverProperties = mock(ServerProperties.class);
        when(servlet.getContextPath()).thenReturn(contextPath);
        when(serverProperties.getServlet()).thenReturn(servlet);
        var token = "9F2FCF5EFC2A026B319D5D267C06D8D06B0C18C1";
        var link = this.jbstProperties.getEmailConfirmationLink(
                serverProperties,
                token
        );

        // Assert
        assertThat(link).isEqualTo("http://127.0.0.1:3002/tests-context-path/jbst/security/tokens/email/confirm?token=" + token);
    }

    @Test
    void getPasswordResetLink() {
        // Act
        var token = "9F2FCF5EFC2A026B319D5D267C06D8D06B0C18C1";
        var link = this.jbstProperties.getPasswordResetLink(
                token
        );

        // Assert
        assertThat(link).isEqualTo("http://127.0.0.1:3000/password-reset?token=" + token);
    }
}
