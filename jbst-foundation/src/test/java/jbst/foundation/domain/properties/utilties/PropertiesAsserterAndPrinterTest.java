package jbst.foundation.domain.properties.utilties;

import jbst.foundation.domain.properties.base.*;
import jbst.foundation.domain.properties.configs.*;
import jbst.foundation.domain.tests.classes.NotUsedPropertiesConfigs;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static java.util.Map.entry;
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
        assertThat(throwable.getMessage()).isEqualTo("Property not-used-properties-configs.types is invalid. Entries: [AUTHENTICATION_LOGIN1=true, AUTHENTICATION_LOGIN2=false]. Size: 2. MinSize: 3");
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
    void incidentManagerConfigsTest() {
        // Act
        IncidentsManager.hardcoded().assertProperties();

        // Assert
        // no asserts
    }

    @Test
    void incidentsCorrectTest() {
        var loginFailureUsernamePassword = randomBoolean();
        var loginFailureUsernameMaskedPassword = !loginFailureUsernamePassword;
        var incidentsManager = new IncidentsManager(
                true,
                IncidentsManagerType.hardcoded(),
                RemoteServer.hardcoded(),
                Map.ofEntries(
                        entry("AUTHENTICATION_LOGIN", randomBoolean()),
                        entry("AUTHENTICATION_LOGIN_FAILURE_USERNAME_PASSWORD", loginFailureUsernamePassword),
                        entry("AUTHENTICATION_LOGIN_FAILURE_USERNAME_MASKED_PASSWORD", loginFailureUsernameMaskedPassword),
                        entry("AUTHENTICATION_LOGOUT", randomBoolean()),
                        entry("AUTHENTICATION_LOGOUT_MIN", randomBoolean()),
                        entry("SESSION_REFRESHED", randomBoolean()),
                        entry("SESSION_EXPIRED", randomBoolean()),
                        entry("REGISTER_MAGICLINK", randomBoolean()),
                        entry("REGISTER0", randomBoolean()),
                        entry("REGISTER0_FAILURE", randomBoolean()),
                        entry("REGISTER1", randomBoolean()),
                        entry("REGISTER1_FAILURE", randomBoolean())
                )
        );

        // Act
        incidentsManager.assertProperties();

        // Assert
        // no asserts
    }

    @Test
    void incidentsNoSessionRefreshedFailureTest() {
        var incidentsManager = new IncidentsManager(
                true,
                IncidentsManagerType.hardcoded(),
                RemoteServer.hardcoded(),
                Map.ofEntries(
                        entry("AUTHENTICATION_LOGIN", true),
                        entry("AUTHENTICATION_LOGIN_FAILURE_USERNAME_PASSWORD", false),
                        entry("AUTHENTICATION_LOGIN_FAILURE_USERNAME_MASKED_PASSWORD", true),
                        entry("AUTHENTICATION_LOGOUT", true),
                        entry("AUTHENTICATION_LOGOUT_MIN", true),
                        entry("SESSION_EXPIRED", true),
                        entry("REGISTER_MAGICLINK", true),
                        entry("REGISTER0", true),
                        entry("REGISTER0_FAILURE", true),
                        entry("REGISTER1", true),
                        entry("REGISTER1_FAILURE", true)
                )
        );

        // Act
        var throwable = catchThrowable(incidentsManager::assertProperties);

        // Assert
        assertThat(throwable).isNotNull();
        assertThat(throwable.getClass()).isEqualTo(IllegalArgumentException.class);
        assertThat(throwable.getMessage()).isEqualTo("Property incidents-manager.types is invalid. Entries: [AUTHENTICATION_LOGIN=true, AUTHENTICATION_LOGIN_FAILURE_USERNAME_MASKED_PASSWORD=true, AUTHENTICATION_LOGIN_FAILURE_USERNAME_PASSWORD=false, AUTHENTICATION_LOGOUT=true, AUTHENTICATION_LOGOUT_MIN=true, REGISTER0=true, REGISTER0_FAILURE=true, REGISTER1=true, REGISTER1_FAILURE=true, REGISTER_MAGICLINK=true, SESSION_EXPIRED=true]. Size: 11. MinSize: 12");
    }

    @Test
    void incidentsOnlyOneLoginFailureTest() {
        var incidentsManager = new IncidentsManager(
                true,
                IncidentsManagerType.hardcoded(),
                RemoteServer.hardcoded(),
                Map.ofEntries(
                        entry("AUTHENTICATION_LOGIN", randomBoolean()),
                        entry("AUTHENTICATION_LOGIN_FAILURE_USERNAME_PASSWORD", true),
                        entry("AUTHENTICATION_LOGIN_FAILURE_USERNAME_MASKED_PASSWORD", true),
                        entry("AUTHENTICATION_LOGOUT", randomBoolean()),
                        entry("AUTHENTICATION_LOGOUT_MIN", randomBoolean()),
                        entry("SESSION_REFRESHED", randomBoolean()),
                        entry("SESSION_EXPIRED", randomBoolean()),
                        entry("REGISTER_MAGICLINK", randomBoolean()),
                        entry("REGISTER0", randomBoolean()),
                        entry("REGISTER0_FAILURE", randomBoolean()),
                        entry("REGISTER1", randomBoolean()),
                        entry("REGISTER1_FAILURE", randomBoolean())
                )
        );

        // Act
        var throwable = catchThrowable(incidentsManager::assertProperties);

        // Assert
        assertThat(throwable).isNotNull();
        assertThat(throwable.getClass()).isEqualTo(IllegalArgumentException.class);
        assertThat(throwable.getMessage()).isEqualTo("[IncidentsConfigs]: one login failure feature type expected to be provided");
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
    void mongodbSecurityJwtConfigsTest() {
        // Act
        MongodbSecurityJwtConfigs.hardcoded().assertProperties();

        // Assert
        // no asserts
    }
}
