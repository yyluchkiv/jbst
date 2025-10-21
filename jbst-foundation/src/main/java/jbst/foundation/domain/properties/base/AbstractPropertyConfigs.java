package jbst.foundation.domain.properties.base;

import jbst.foundation.domain.reflections.JbstPropertiesUtility;
import jbst.foundation.domain.reflections.JbstProperty;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.util.List;

import static jbst.foundation.domain.asserts.ConsoleAsserts.assertNonNullOrThrow;

// TODO [YYL] merge AbstractPropertyConfigs + AbstractPropertiesConfigs: [parent, leaf, name]?
@Slf4j
public abstract class AbstractPropertyConfigs {

    public void assertProperties(String propertyName) {
        this.assertFields(propertyName, JbstPropertiesUtility.getMandatoryFields(this, propertyName));
    }

    // =================================================================================================================
    // PROTECTED METHODS
    // =================================================================================================================
    protected void assertFields(String propertyName, List<Field> fields) {
        fields.forEach(field -> {
            try {
                var jbstProperty = new JbstProperty(propertyName, field, field.get(this));
                assertNonNullOrThrow(jbstProperty);
                jbstProperty.verify();
            } catch (IllegalAccessException ex) {
                throw new IllegalArgumentException(ex);
            }
        });
    }
}
