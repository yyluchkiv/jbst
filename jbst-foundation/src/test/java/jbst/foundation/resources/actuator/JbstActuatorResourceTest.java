package jbst.foundation.resources.actuator;

import jbst.foundation.configurations.TestJbstConfigurationPropertiesHardcoded;
import jbst.foundation.domain.properties.JbstProperties;
import jbst.foundation.resources.system.JbstActuatorResource;
import jbst.foundation.utils.JbstEnvUtils;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import java.util.Map;

import static jbst.foundation.domain.random.JbstRandom.randomString;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith({ SpringExtension.class })
@ContextConfiguration(loader= AnnotationConfigContextLoader.class)
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
class JbstActuatorResourceTest {

    @Configuration
    @Import({
            TestJbstConfigurationPropertiesHardcoded.class
    })
    @RequiredArgsConstructor(onConstructor = @__(@Autowired))
    static class ContextConfiguration {
        private final JbstProperties jbstProperties;

        @Bean
        JbstEnvUtils envUtils() {
            return mock(JbstEnvUtils.class);
        }

        @Bean
        JbstActuatorResource baseInfoResource() {
            return new JbstActuatorResource(
                    this.envUtils(),
                    this.jbstProperties
            );
        }
    }

    // Utilities
    private final JbstEnvUtils envUtils;
    // Properties
    private final JbstProperties jbstProperties;

    private final JbstActuatorResource componentUnderTest;

    @BeforeEach
    void beforeEach() {
        reset(
                this.envUtils
        );
    }

    @AfterEach
    void afterEach() {
        verifyNoMoreInteractions(
                this.envUtils
        );
    }

    @SuppressWarnings("unchecked")
    @Test
    void verifyProfilesConfigurationExceptionTest() {
        // Arrange
        var activeProfile = randomString();
        var builder = mock(Info.Builder.class);
        when(this.envUtils.getOneActiveProfileOrDash()).thenReturn(activeProfile);

        // Act
        this.componentUnderTest.contribute(builder);

        // Assert
        verify(this.envUtils).getOneActiveProfileOrDash();
        var builderDetailsAC = ArgumentCaptor.forClass(Map.class);
        verify(builder).withDetails(builderDetailsAC.capture());
        var details = builderDetailsAC.getValue();
        assertThat(details)
                .hasSize(2)
                .containsEntry("activeProfile", activeProfile)
                .containsEntry("maven", this.jbstProperties.getApp().getMaven().asMavenDetails());
    }
}
