package jbst.foundation.domain.properties.configs;

import jbst.foundation.domain.asserts.ConsoleAsserts;
import jbst.foundation.domain.properties.base.AbstractPropertyConfigs;
import jbst.foundation.domain.reflections.JbstProperty;

import java.lang.reflect.Field;
import java.util.List;

import static java.util.Objects.isNull;
import static jbst.foundation.domain.properties.utilities.PropertiesAsserter.*;

public abstract class AbstractPropertiesConfigs {
    public abstract boolean isParentPropertiesNode();
    public abstract String getPropertyName();

    public void assertProperties() {
        this.assertFields(getMandatoryFields(this, this.getPropertyName()));
        if (this.isParentPropertiesNode()) {
            this.printProperties();
        }
    }

    public void printProperties() {
        getMandatoryBasedFields(this, this.getPropertyName()).forEach(field -> {
            try {
                var rf = new JbstProperty(this.getPropertyName(), field, field.get(this));
                if (isNull(rf.getPropertyValue())) {
                    rf.print();
                } else {
                    var nestedPropertyClass = rf.getPropertyValue().getClass();
                    if (AbstractPropertiesConfigs.class.isAssignableFrom(nestedPropertyClass)) {
                        ((AbstractPropertiesConfigs) rf.getPropertyValue()).printProperties();
                    } else if (AbstractPropertyConfigs.class.isAssignableFrom(nestedPropertyClass)) {
                        ((AbstractPropertyConfigs) rf.getPropertyValue()).printProperties(rf.getTreePropertyName());
                    } else {
                        rf.print();
                    }
                }
            } catch (IllegalAccessException ex) {
                throw new IllegalArgumentException(ex);
            }
        });
    }

    // =================================================================================================================
    // PRIVATE METHODS
    // =================================================================================================================
    public void assertFields(List<Field> fields) {
        fields.forEach(field -> {
            try {
                var jbstProperty = new JbstProperty(this.getPropertyName(), field, field.get(this));
                ConsoleAsserts.assertNonNullOrThrow(jbstProperty);
                var nestedPropertyClass = jbstProperty.getPropertyValue().getClass();
                if (AbstractPropertiesConfigs.class.isAssignableFrom(nestedPropertyClass)) {
                    ((AbstractPropertiesConfigs) jbstProperty.getPropertyValue()).assertProperties();
                } else if (AbstractPropertyConfigs.class.isAssignableFrom(nestedPropertyClass)) {
                    ((AbstractPropertyConfigs) jbstProperty.getPropertyValue()).assertProperties(jbstProperty.getTreePropertyName());
                } else {
                    verifyProperty(jbstProperty);
                }
            } catch (IllegalAccessException ex) {
                throw new IllegalArgumentException(ex);
            }
        });
    }
}
