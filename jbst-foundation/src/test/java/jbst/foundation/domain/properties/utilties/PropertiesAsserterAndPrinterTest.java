package jbst.foundation.domain.properties.utilties;

import jbst.foundation.domain.properties.base.ScheduledJob;
import jbst.foundation.domain.properties.base.SchedulerConfiguration;
import jbst.foundation.domain.properties.base.SpringLogging;
import jbst.foundation.domain.properties.base.SpringServer;
import jbst.foundation.domain.properties.configs.*;
import jbst.foundation.domain.properties.configs.security.jwt.IncidentsConfigs;
import jbst.foundation.domain.tests.classes.NotUsedPropertiesConfigs;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static java.util.Map.entry;
import static jbst.foundation.domain.properties.base.JbstIamIncidentType.*;
import static jbst.foundation.utilities.random.RandomUtility.randomBoolean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.catchThrowable;

class PropertiesAsserterAndPrinterTest {

    @RepeatedTest(10)
    void notUsedPropertiesConfigsMapMinSizeCase() {
        // Arrange
        var notUsedPropertiesConfigs = new NotUsedPropertiesConfigs(
                new ScheduledJob(true, SchedulerConfiguration.hardcoded()),
                new SpringServer(8080),
                new SpringLogging("logback-test.xml"),
                Map.ofEntries(
                        Map.entry("AUTHENTICATION_LOGIN1", true),
                        Map.entry("AUTHENTICATION_LOGIN2", false)
                )
        );

        // Act
        var throwable = catchThrowable(notUsedPropertiesConfigs::assertProperties);

        // Assert
        assertThat(throwable).isNotNull();
        assertThat(throwable.getClass()).isEqualTo(IllegalArgumentException.class);
        assertThat(throwable.getMessage()).isEqualTo("Property not-used-properties-configs.types is invalid. Entries: [AUTHENTICATION_LOGIN1=true, AUTHENTICATION_LOGIN2=false]. MinSize: 3");
    }

    @RepeatedTest(10)
    void notUsedPropertiesConfigsExtendedSizeCase() {
        // Arrange
        var notUsedPropertiesConfigs = new NotUsedPropertiesConfigs(
                new ScheduledJob(true, SchedulerConfiguration.hardcoded()),
                new SpringServer(8080),
                new SpringLogging("logback-test.xml"),
                Map.ofEntries(
                        Map.entry("AUTHENTICATION_LOGIN1", true),
                        Map.entry("AUTHENTICATION_LOGIN2", false),
                        Map.entry("AUTHENTICATION_LOGIN3", false)
                )
        );

        // Act
        var throwable = catchThrowable(() -> {
            notUsedPropertiesConfigs.assertProperties();
            notUsedPropertiesConfigs.assertPropertiesExtended(4);
        });

        // Assert
        assertThat(throwable).isNotNull();
        assertThat(throwable.getClass()).isEqualTo(IllegalArgumentException.class);
        assertThat(throwable.getMessage()).isEqualTo("Property not-used-properties-configs.types is invalid. Entries: [AUTHENTICATION_LOGIN1=true, AUTHENTICATION_LOGIN2=false, AUTHENTICATION_LOGIN3=false]. MinSize: 4");
    }

    @RepeatedTest(10)
    void notUsedPropertiesConfigsOK() {
        // Arrange
        var notUsedPropertiesConfigs = new NotUsedPropertiesConfigs(
                new ScheduledJob(true, SchedulerConfiguration.hardcoded()),
                new SpringServer(8080),
                new SpringLogging("logback-test.xml"),
                Map.ofEntries(
                        Map.entry("AUTHENTICATION_LOGIN1", true),
                        Map.entry("AUTHENTICATION_LOGIN2", false),
                        Map.entry("AUTHENTICATION_LOGIN3", false),
                        Map.entry("AUTHENTICATION_LOGIN4", false)
                )
        );

        // Act
        var throwable = catchThrowable(() -> {
            notUsedPropertiesConfigs.assertProperties();
            notUsedPropertiesConfigs.assertPropertiesExtended(4);
        });

        // Assert
        assertThat(throwable).isNull();
    }

    @Test
    void serverConfigsTest() {
        // Act
        ServerConfigs.hardcoded().assertProperties();

        // Assert
        // no asserts
    }

    @Test
    void utilitiesConfigsTest() {
        // Act
        UtilsConfigs.hardcoded().assertProperties();

        // Assert
        // no asserts
    }

    @Test
    void asyncConfigsTest() {
        // Act
        AsyncConfigs.hardcoded().assertProperties();

        // Assert
        // no asserts
    }

    @Test
    void eventsConfigsTest() {
        // Act
        EventsConfigs.hardcoded().assertProperties();

        // Assert
        // no asserts
    }

    @Test
    void mvcConfigsDisabledTest() {
        // Arrange
        var mvcConfigs = new MvcConfigs(false, null, null);

        // Act
        mvcConfigs.assertProperties();

        // Assert
        // no asserts
    }

    @Test
    void mvcConfigsTest() {
        // Act
        MvcConfigs.hardcoded().assertProperties();

        // Assert
        // no asserts
    }

    @Test
    void emailConfigsDisabledTest() {
        // Act
        EmailConfigs.disabled().assertProperties();

        // Assert
        // no asserts
    }

    @Test
    void emailConfigsTest() {
        // Act
        EmailConfigs.hardcoded().assertProperties();

        // Assert
        // no asserts
    }

    @Test
    void incidentConfigsTest() {
        // Act
        IncidentsManagerConfigs.hardcoded().assertProperties();

        // Assert
        // no asserts
    }

    @Test
    void securityJwtConfigsDisabledUsersEmailsConfigsTest() {
        // Act
        var securityJwtConfigs = SecurityJwtConfigs.disabledUsersEmailsConfigs();

        // Act
        var throwable = catchThrowable(securityJwtConfigs::assertProperties);

        // Assert
        assertThat(throwable).isNotNull();
        assertThat(throwable.getClass()).isEqualTo(IllegalArgumentException.class);
        assertThat(throwable.getMessage()).startsWith("Property");
        assertThat(throwable.getMessage()).endsWith(" is null");
    }

    @Test
    void securityJwtConfigsTest() {
        // Act
        SecurityJwtConfigs.hardcoded().assertProperties();

        // Assert
        // no asserts
    }

    @Test
    void securityJwtConfigsIncidentsCorrectTest() {
        var loginFailureUsernamePassword = randomBoolean();
        var loginFailureUsernameMaskedPassword = !loginFailureUsernamePassword;
        var incidentConfigs = new IncidentsConfigs(
                Map.ofEntries(
                        entry(AUTHENTICATION_LOGIN, randomBoolean()),
                        entry(AUTHENTICATION_LOGIN_FAILURE_USERNAME_PASSWORD, loginFailureUsernamePassword),
                        entry(AUTHENTICATION_LOGIN_FAILURE_USERNAME_MASKED_PASSWORD, loginFailureUsernameMaskedPassword),
                        entry(AUTHENTICATION_LOGOUT, randomBoolean()),
                        entry(AUTHENTICATION_LOGOUT_MIN, randomBoolean()),
                        entry(SESSION_REFRESHED, randomBoolean()),
                        entry(SESSION_EXPIRED, randomBoolean()),
                        entry(REGISTER_MAGICLINK, randomBoolean()),
                        entry(REGISTER0, randomBoolean()),
                        entry(REGISTER0_FAILURE, randomBoolean()),
                        entry(REGISTER1, randomBoolean()),
                        entry(REGISTER1_FAILURE, randomBoolean())
                )
        );
        var securityJwtConfigs = new SecurityJwtConfigs(
                SecurityJwtConfigs.hardcoded().getAuthoritiesConfigs(),
                SecurityJwtConfigs.hardcoded().getCookiesConfigs(),
                SecurityJwtConfigs.hardcoded().getEssenceConfigs(),
                incidentConfigs,
                SecurityJwtConfigs.hardcoded().getJwtTokensConfigs(),
                SecurityJwtConfigs.hardcoded().getLoggingConfigs(),
                SecurityJwtConfigs.hardcoded().getSessionConfigs(),
                SecurityJwtConfigs.hardcoded().getUsersEmailsConfigs(),
                SecurityJwtConfigs.hardcoded().getWebsocketsConfigs(),
                SecurityJwtConfigs.hardcoded().getUsersTokensConfigs()
        );

        // Act
        securityJwtConfigs.assertProperties();

        // Assert
        // no asserts
    }

    @Test
    void securityJwtConfigsIncidentsNoSessionRefreshedFailureTest() {
        var incidentConfigs = new IncidentsConfigs(
                Map.ofEntries(
                    entry(AUTHENTICATION_LOGIN, randomBoolean()),
                    entry(AUTHENTICATION_LOGIN_FAILURE_USERNAME_PASSWORD, false),
                    entry(AUTHENTICATION_LOGIN_FAILURE_USERNAME_MASKED_PASSWORD, true),
                    entry(AUTHENTICATION_LOGOUT, randomBoolean()),
                    entry(AUTHENTICATION_LOGOUT_MIN, randomBoolean()),
                    entry(SESSION_EXPIRED, randomBoolean()),
                    entry(REGISTER_MAGICLINK, randomBoolean()),
                    entry(REGISTER0, randomBoolean()),
                    entry(REGISTER0_FAILURE, randomBoolean()),
                    entry(REGISTER1, randomBoolean()),
                    entry(REGISTER1_FAILURE, randomBoolean())
                )
        );
        var securityJwtConfigs = new SecurityJwtConfigs(
                SecurityJwtConfigs.hardcoded().getAuthoritiesConfigs(),
                SecurityJwtConfigs.hardcoded().getCookiesConfigs(),
                SecurityJwtConfigs.hardcoded().getEssenceConfigs(),
                incidentConfigs,
                SecurityJwtConfigs.hardcoded().getJwtTokensConfigs(),
                SecurityJwtConfigs.hardcoded().getLoggingConfigs(),
                SecurityJwtConfigs.hardcoded().getSessionConfigs(),
                SecurityJwtConfigs.hardcoded().getUsersEmailsConfigs(),
                SecurityJwtConfigs.hardcoded().getWebsocketsConfigs(),
                SecurityJwtConfigs.hardcoded().getUsersTokensConfigs()
        );

        // Act
        var throwable = catchThrowable(securityJwtConfigs::assertProperties);

        // Assert
        assertThat(throwable).isNotNull();
        assertThat(throwable.getClass()).isEqualTo(IllegalArgumentException.class);
        assertThat(throwable.getMessage()).isEqualTo("Property security-jwt-configs.incidents-configs.types-configs is invalid. Options: [Authentication Login, Authentication Login Failure Username/Masked Password, Authentication Login Failure Username/Password, Authentication Logout, Authentication Logout Min, Register MagicLink, Register0, Register0 Failure, Register1, Register1 Failure, Session Expired, Session Refreshed]. Required: [Authentication Login, Authentication Login Failure Username/Masked Password, Authentication Login Failure Username/Password, Authentication Logout, Authentication Logout Min, Register MagicLink, Register0, Register0 Failure, Register1, Register1 Failure, Session Expired]. Disjunction: [\u001B[31mSession Refreshed\u001B[0m]");
    }

    @Test
    void securityJwtConfigsIncidentsOnlyOneLoginFailureTest() {
        var incidentConfigs = new IncidentsConfigs(
                Map.ofEntries(
                    entry(AUTHENTICATION_LOGIN, randomBoolean()),
                    entry(AUTHENTICATION_LOGIN_FAILURE_USERNAME_PASSWORD, true),
                    entry(AUTHENTICATION_LOGIN_FAILURE_USERNAME_MASKED_PASSWORD, true),
                    entry(AUTHENTICATION_LOGOUT, randomBoolean()),
                    entry(AUTHENTICATION_LOGOUT_MIN, randomBoolean()),
                    entry(SESSION_REFRESHED, randomBoolean()),
                    entry(SESSION_EXPIRED, randomBoolean()),
                    entry(REGISTER_MAGICLINK, randomBoolean()),
                    entry(REGISTER0, randomBoolean()),
                    entry(REGISTER0_FAILURE, randomBoolean()),
                    entry(REGISTER1, randomBoolean()),
                    entry(REGISTER1_FAILURE, randomBoolean())
                )
        );
        var securityJwtConfigs = new SecurityJwtConfigs(
                SecurityJwtConfigs.hardcoded().getAuthoritiesConfigs(),
                SecurityJwtConfigs.hardcoded().getCookiesConfigs(),
                SecurityJwtConfigs.hardcoded().getEssenceConfigs(),
                incidentConfigs,
                SecurityJwtConfigs.hardcoded().getJwtTokensConfigs(),
                SecurityJwtConfigs.hardcoded().getLoggingConfigs(),
                SecurityJwtConfigs.hardcoded().getSessionConfigs(),
                SecurityJwtConfigs.hardcoded().getUsersEmailsConfigs(),
                SecurityJwtConfigs.hardcoded().getWebsocketsConfigs(),
                SecurityJwtConfigs.hardcoded().getUsersTokensConfigs()
        );

        // Act
        var throwable = catchThrowable(securityJwtConfigs::assertProperties);

        // Assert
        assertThat(throwable).isNotNull();
        assertThat(throwable.getClass()).isEqualTo(IllegalArgumentException.class);
        assertThat(throwable.getMessage()).isEqualTo("[IncidentsConfigs]: one login failure feature type expected to be provided");
    }

    @Test
    void mongodbSecurityJwtConfigsTest() {
        // Act
        MongodbSecurityJwtConfigs.hardcoded().assertProperties();

        // Assert
        // no asserts
    }
}
