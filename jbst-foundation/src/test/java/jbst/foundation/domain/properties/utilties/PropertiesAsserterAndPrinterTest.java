package jbst.foundation.domain.properties.utilties;

import jbst.foundation.domain.enums.JbstIncidentsManagerType;
import jbst.foundation.domain.properties.JbstProperty;
import jbst.foundation.domain.properties.annotations.JbstPropertyMandatory;
import jbst.foundation.domain.properties.annotations.JbstPropertyMetadataMapMinSize;
import jbst.foundation.domain.properties.annotations.JbstPropertyOptional;
import jbst.foundation.domain.properties.base.*;
import jbst.foundation.domain.properties.configs.*;
import jbst.foundation.domain.properties.configs.databases.JbstPropertyDatabaseMongo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.springframework.boot.context.properties.bind.ConstructorBinding;

import java.util.Map;
import java.util.Set;

import static java.util.Map.entry;
import static jbst.foundation.domain.asserts.Asserts.assertTrueOrThrow;
import static jbst.foundation.domain.collections.JbstCollections.baseJoiningRaw;
import static jbst.foundation.domain.constants.JbstConstants.JColor.RED_TEXT;
import static jbst.foundation.domain.random.JbstRandom.randomBoolean;
import static org.apache.commons.collections4.SetUtils.disjunction;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.catchThrowable;

class PropertiesAsserterAndPrinterTest {

    @RepeatedTest(10)
    void notUsedPropertiesConfigsMapMinSizeNullableCase() {
        // Arrange
        var notUsedPropertiesConfigs = new NotUsedPropertiesConfigsIncidentsMinSize0Nullable(
                new JbstPropertyScheduledJob(true, JbstPropertySchedulerConfiguration.hardcoded()),
                new JbstPropertySpringServer(8080),
                new JbstPropertySpringLogging("logback-test.xml"),
                null
        );

        // Act
        var throwable = catchThrowable(notUsedPropertiesConfigs::assertProperties);

        // Assert
        assertThat(throwable).isNull();
    }
    @RepeatedTest(10)
    void notUsedPropertiesConfigsMapMinSizeCase() {
        // Arrange
        var notUsedPropertiesConfigs = new NotUsedPropertiesConfigsIncidentsMinSize3(
                new JbstPropertyScheduledJob(true, JbstPropertySchedulerConfiguration.hardcoded()),
                new JbstPropertySpringServer(8080),
                new JbstPropertySpringLogging("logback-test.xml"),
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
        var notUsedPropertiesConfigs = new NotUsedPropertiesConfigsIncidentsMinSize3(
                new JbstPropertyScheduledJob(true, JbstPropertySchedulerConfiguration.hardcoded()),
                new JbstPropertySpringServer(8080),
                new JbstPropertySpringLogging("logback-test.xml"),
                Map.ofEntries(
                        Map.entry("AUTHENTICATION_LOGIN1", true),
                        Map.entry("AUTHENTICATION_LOGIN2", false),
                        Map.entry("AUTHENTICATION_LOGIN3", false)
                )
        );

        // Act
        var throwable = catchThrowable(() -> {
            notUsedPropertiesConfigs.assertProperties();
            notUsedPropertiesConfigs.assertPropertiesExtended(Set.of("AUTHENTICATION_LOGIN1", "AUTHENTICATION_LOGIN2", "AUTHENTICATION_LOGIN3", "EXTENDED_LOGIN"));
        });

        // Assert
        assertThat(throwable).isNotNull();
        assertThat(throwable.getClass()).isEqualTo(IllegalArgumentException.class);
        assertThat(throwable.getMessage()).isEqualTo("Property not-used-properties-configs.types is invalid. Options: [AUTHENTICATION_LOGIN1=true, AUTHENTICATION_LOGIN2=false, AUTHENTICATION_LOGIN3=false]. Required: [AUTHENTICATION_LOGIN1, AUTHENTICATION_LOGIN2, AUTHENTICATION_LOGIN3, EXTENDED_LOGIN]. Disjunction: [[31mEXTENDED_LOGIN[0m]");
    }

    @RepeatedTest(10)
    void notUsedPropertiesConfigsOK() {
        // Arrange
        var notUsedPropertiesConfigs = new NotUsedPropertiesConfigsIncidentsMinSize3(
                new JbstPropertyScheduledJob(true, JbstPropertySchedulerConfiguration.hardcoded()),
                new JbstPropertySpringServer(8080),
                new JbstPropertySpringLogging("logback-test.xml"),
                Map.ofEntries(
                        Map.entry("AUTHENTICATION_LOGIN1", true),
                        Map.entry("AUTHENTICATION_LOGIN2", false),
                        Map.entry("AUTHENTICATION_LOGIN3", false),
                        Map.entry("EXTENDED_LOGIN", false)
                )
        );

        // Act
        var throwable = catchThrowable(() -> {
            notUsedPropertiesConfigs.assertProperties();
            notUsedPropertiesConfigs.assertPropertiesExtended(Set.of("AUTHENTICATION_LOGIN1", "AUTHENTICATION_LOGIN2", "AUTHENTICATION_LOGIN3", "EXTENDED_LOGIN"));
        });

        // Assert
        assertThat(throwable).isNull();
    }

    @Test
    void appTest() {
        // Act
        JbstPropertyApp.hardcoded().assertProperties();

        // Assert
        // no asserts
    }

    @Test
    void utilsTest() {
        // Act
        JbstPropertyUtils.hardcoded().assertProperties();

        // Assert
        // no asserts
    }

    @Test
    void asyncTest() {
        // Act
        JbstPropertyAsync.hardcoded().assertProperties();

        // Assert
        // no asserts
    }

    @Test
    void eventsTest() {
        // Act
        JbstPropertyEvents.hardcoded().assertProperties();

        // Assert
        // no asserts
    }

    @Test
    void mvcDisabledTest() {
        // Arrange
        var mvc = new JbstPropertyMVC(false, null, null);

        // Act
        mvc.assertProperties();

        // Assert
        // no asserts
    }

    @Test
    void mvcTest() {
        // Act
        JbstPropertyMVC.hardcoded().assertProperties();

        // Assert
        // no asserts
    }

    @Test
    void emailsDisabledTest() {
        // Act
        JbstPropertyEmails.disabled().assertProperties();

        // Assert
        // no asserts
    }

    @Test
    void emailsTest() {
        // Act
        JbstPropertyEmails.hardcoded().assertProperties();

        // Assert
        // no asserts
    }

    @Test
    void incidentManagerTest() {
        // Act
        JbstPropertyIncidentsManager.hardcoded().assertProperties();

        // Assert
        // no asserts
    }

    @Test
    void incidentManagerJbst() {
        var loginFailureUsernamePassword = randomBoolean();
        var loginFailureUsernameMaskedPassword = !loginFailureUsernamePassword;
        var incidentsManager = new JbstPropertyIncidentsManager(
                true,
                JbstIncidentsManagerType.hardcoded(),
                JbstPropertyRemoteServer.hardcoded(),
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
    void incidentManagerZero() {
        var incidentsManager = new JbstPropertyIncidentsManager(
                true,
                JbstIncidentsManagerType.hardcoded(),
                JbstPropertyRemoteServer.hardcoded(),
                Map.ofEntries()
        );

        // Act
        incidentsManager.assertProperties();

        // Assert
        // no asserts
    }

    @Test
    void securityDisabledUsersEmailsConfigsTest() {
        // Act
        var security = JbstPropertySecurity.disabledUsersEmails();

        // Act
        var throwable = catchThrowable(security::assertProperties);

        // Assert
        assertThat(throwable).isNotNull();
        assertThat(throwable.getClass()).isEqualTo(IllegalArgumentException.class);
        assertThat(throwable.getMessage()).startsWith("Property");
        assertThat(throwable.getMessage()).endsWith(" is null");
    }

    @Test
    void securityTest() {
        // Act
        JbstPropertySecurity.hardcoded().assertProperties();

        // Assert
        // no asserts
    }

    @Test
    void databaseMongoTest() {
        // Act
        JbstPropertyDatabaseMongo.hardcoded().assertProperties();

        // Assert
        // no asserts
    }

    // =================================================================================================================
    // PRIVATE METHODS
    // =================================================================================================================
    @AllArgsConstructor(onConstructor = @__({@ConstructorBinding}))
    @Data
    @EqualsAndHashCode(callSuper = true)
    public static class NotUsedPropertiesConfigsIncidentsMinSize0Nullable extends JbstProperty {
        @JbstPropertyMandatory
        private final JbstPropertyScheduledJob scheduledJob;
        @JbstPropertyMandatory
        private final JbstPropertySpringServer springServer;
        @JbstPropertyMandatory
        private final JbstPropertySpringLogging springLogging;
        @JbstPropertyOptional
        @JbstPropertyMetadataMapMinSize(minSize = 0)
        private final Map<String, Boolean> types;

        @Override
        public JbstPropertyNodeType getNodeType() {
            return JbstPropertyNodeType.ROOT;
        }

        @Override
        public boolean isToggle() {
            return false;
        }

        @Override
        public String getNameNonLeaf() {
            return "not-used-properties-configs";
        }
    }

    @AllArgsConstructor(onConstructor = @__({@ConstructorBinding}))
    @Data
    @EqualsAndHashCode(callSuper = true)
    public static class NotUsedPropertiesConfigsIncidentsMinSize3 extends JbstProperty {
        @JbstPropertyMandatory
        private final JbstPropertyScheduledJob scheduledJob;
        @JbstPropertyMandatory
        private final JbstPropertySpringServer springServer;
        @JbstPropertyMandatory
        private final JbstPropertySpringLogging springLogging;
        @JbstPropertyMandatory
        @JbstPropertyMetadataMapMinSize(minSize = 3)
        private final Map<String, Boolean> types;

        @Override
        public JbstPropertyNodeType getNodeType() {
            return JbstPropertyNodeType.ROOT;
        }

        @Override
        public boolean isToggle() {
            return false;
        }

        @Override
        public String getNameNonLeaf() {
            return "not-used-properties-configs";
        }

        public void assertPropertiesExtended(Set<String> keys) {
            assertTrueOrThrow(
                    this.types.size() >= keys.size(),
                    "Property %s is invalid. Options: [%s]. Required: [%s]. Disjunction: [%s]".formatted(
                            "not-used-properties-configs.types",
                            baseJoiningRaw(this.types.entrySet()),
                            baseJoiningRaw(keys),
                            RED_TEXT.format(baseJoiningRaw(disjunction(this.types.keySet(), keys)))
                    )
            );
        }
    }
}
