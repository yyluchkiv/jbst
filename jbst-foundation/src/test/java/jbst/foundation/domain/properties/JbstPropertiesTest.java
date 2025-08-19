package jbst.foundation.domain.properties;

import jbst.foundation.configurations.TestJbstConfigurationPropertiesHardcoded;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;

import static jbst.foundation.utilities.reflections.ReflectionUtility.getGetters;
import static org.assertj.core.api.Assertions.assertThat;

class JbstPropertiesTest {

    private final JbstProperties jbstProperties = new TestJbstConfigurationPropertiesHardcoded().jbstProperties();

    @Test
    void jbstPropertiesTest() {
        // Arrange
        var jbstProperties = new TestJbstConfigurationPropertiesHardcoded().jbstProperties();

        // Act
        var getters = getGetters(jbstProperties);

        // Assert
        assertThat(getters).hasSize(13);
        getters.forEach(getter -> {
            try {
                var propertiesConfigs = getter.invoke(jbstProperties);
                assertThat(propertiesConfigs).isNotNull();
            } catch (IllegalAccessException | InvocationTargetException ex) {
                throw new RuntimeException(ex);
            }
        });
    }

    @Test
    void getEmailConfirmationRedirectLink() {
        // Act
        var link = this.jbstProperties.getEmailConfirmationRedirectLink();

        // Assert
        assertThat(link).isEqualTo("http://127.0.0.1:3000/email-confirmation");
    }
}
