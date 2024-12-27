package jbst.foundation.domain.properties;

import jbst.foundation.configurations.TestJbstConfigurationPropertiesHardcoded;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;

import static jbst.foundation.utilities.reflections.ReflectionUtility.getGetters;
import static org.assertj.core.api.Assertions.assertThat;

class JbstPropertiesTest {

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
}
