package jbst.foundation.domain.properties;

import jbst.foundation.domain.annotations.JbstNonMandatoryMethod;
import jbst.foundation.domain.properties.annotations.MandatoryProperty;
import jbst.foundation.domain.properties.annotations.MandatoryToggleProperty;
import jbst.foundation.domain.properties.annotations.NonMandatoryProperty;
import jbst.foundation.domain.properties.base.AbstractPropertyConfigs;
import jbst.foundation.domain.properties.configs.AbstractPropertiesConfigs;
import jbst.foundation.domain.reflections.JbstProperty;
import org.springframework.security.core.parameters.P;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Set;

import static java.util.Objects.requireNonNull;
import static jbst.foundation.domain.asserts.ConsoleAsserts.assertNonNullOrThrow;
import static jbst.foundation.utilities.reflections.ReflectionUtility.getFields;

// TODO [YYL] merge AbstractPropertyConfigs + AbstractPropertiesConfigs: [parent, leaf, name]?
public abstract class AbstractJbstProperty {
    public abstract boolean isParent();
    public abstract boolean isLeaf();
    public abstract boolean isToggle();
    @JbstNonMandatoryMethod
    public abstract String getNameNonMandatory();

    // TODO [YYL] fixme?
    public void assertProperties() {
        if (this.isLeaf()) {
            var fields = this.isToggle() ? AbstractJbstProperty.
            if (this.isToggle()) {

            } else {

            }
        } else {
            if (this.isToggle()) {

            } else {

            }
        }
        if (this.isParent()) {
            // TODO [YYL]
            // this.printProperties();
        }
    }

    private void assertProperties(String parentPropertyName) {
        if (this.isLeaf()) {
            var fields = this.isToggle() ? getMandatoryToggleFields(this, parentPropertyName) : getMandatoryFields(this, parentPropertyName);
            fields.forEach(field -> {
                try {
                    var jbstProperty = new JbstProperty(parentPropertyName, field, field.get(this));
                    assertNonNullOrThrow(jbstProperty);
                    jbstProperty.verify();
                } catch (IllegalAccessException ex) {
                    throw new IllegalArgumentException(ex);
                }
            });
        }
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
                } else if (AbstractPropertyConfigs.class.isAssignableFrom(nestedPropertyClass)) {
                    ((AbstractPropertyConfigs) jbstProperty.getPropertyValue()).assertProperties(jbstProperty.getTreePropertyName());
                } else {
                    jbstProperty.verify();
                }
            } catch (IllegalAccessException ex) {
                throw new IllegalArgumentException(ex);
            }
        });
    }

    // =================================================================================================================
    // PRIVATE METHODS
    // =================================================================================================================
    private static List<Field> getMandatoryFields(Object property, String propertyName) {
        assertNonNullOrThrow(property, propertyName);
        return getFields(property, Set.of(MandatoryProperty.class));
    }

    private static List<Field> getMandatoryToggleFields(Object property, String propertyName) {
        assertNonNullOrThrow(property, propertyName);
        return getFields(property, Set.of(MandatoryProperty.class, MandatoryToggleProperty.class));
    }

    private static List<Field> getMandatoryBasedFields(Object property, String propertyName) {
        assertNonNullOrThrow(property, propertyName);
        return getFields(property, Set.of(MandatoryProperty.class, NonMandatoryProperty.class, MandatoryToggleProperty.class));
    }
}
