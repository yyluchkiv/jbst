package jbst.foundation.domain.properties.configs;

import jbst.foundation.domain.reflections.JbstPropertiesUtility;
import jbst.foundation.domain.reflections.JbstProperty;

import java.lang.reflect.Field;
import java.util.List;

import static java.util.Objects.isNull;
import static java.util.Objects.requireNonNull;
import static jbst.foundation.domain.asserts.ConsoleAsserts.assertNonNullOrThrow;

public abstract class AbstractPropertiesConfigs {
    public abstract boolean isParentPropertiesNode();
    public abstract String getPropertyName();

    public void assertProperties() {
        this.assertFields(JbstPropertiesUtility.getMandatoryFields(this, this.getPropertyName()));
        if (this.isParentPropertiesNode()) {
            this.printProperties();
        }
    }

    public void printProperties() {
        JbstPropertiesUtility.getMandatoryBasedFields(this, this.getPropertyName()).forEach(field -> {
            try {
                var jbstProperty = new JbstProperty(this.getPropertyName(), field, field.get(this));
                if (isNull(jbstProperty.getPropertyValue())) {
                    jbstProperty.print();
                } else {
                    var nestedPropertyClass = jbstProperty.getPropertyValue().getClass();
                    if (AbstractPropertiesConfigs.class.isAssignableFrom(nestedPropertyClass)) {
                        ((AbstractPropertiesConfigs) jbstProperty.getPropertyValue()).printProperties();
                    } /* else if (AbstractPropertyConfigs.class.isAssignableFrom(nestedPropertyClass)) {
                        jbstProperty.printAbstractPropertyConfigs();
                    } */ else {
                        jbstProperty.print();
                    }
                }
            } catch (IllegalAccessException ex) {
                throw new IllegalArgumentException(ex);
            }
        });
    }

    // =================================================================================================================
    // PROTECTED METHODS
    // =================================================================================================================
    protected void assertFields(List<Field> fields) {
        fields.forEach(field -> {
            try {
                var jbstProperty = new JbstProperty(this.getPropertyName(), field, field.get(this));
                assertNonNullOrThrow(jbstProperty);
                var nestedPropertyClass = requireNonNull(jbstProperty.getPropertyValue()).getClass();
                if (AbstractPropertiesConfigs.class.isAssignableFrom(nestedPropertyClass)) {
                    ((AbstractPropertiesConfigs) jbstProperty.getPropertyValue()).assertProperties();
                } /* else if (AbstractPropertyConfigs.class.isAssignableFrom(nestedPropertyClass)) {
                    ((AbstractPropertyConfigs) jbstProperty.getPropertyValue()).assertProperties(jbstProperty.getTreePropertyName());
                } */ else {
                    jbstProperty.verify();
                }
            } catch (IllegalAccessException ex) {
                throw new IllegalArgumentException(ex);
            }
        });
    }
}
