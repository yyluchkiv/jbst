package jbst.foundation.domain.properties;

import jbst.foundation.domain.annotations.JbstNonMandatoryMethod;
import jbst.foundation.domain.properties.annotations.MandatoryProperty;
import jbst.foundation.domain.properties.annotations.MandatoryToggleProperty;
import jbst.foundation.domain.properties.annotations.NonMandatoryProperty;
import jbst.foundation.domain.reflections.JbstPropertyEdge;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Set;

import static java.util.Objects.isNull;
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
            return;
        }
        var fields = this.isToggle() ?
                getMandatoryToggleFields(this, this.getNameNonMandatory()) :
                getMandatoryFields(this, this.getNameNonMandatory());
        fields.forEach(field -> {
            try {
                var jbstProperty = new JbstPropertyEdge(this, field, field.get(this));
                assertNonNullOrThrow(jbstProperty);
                var nestedPropertyClass = requireNonNull(jbstProperty.getPropertyValue()).getClass();
//                if (AbstractPropertiesConfigs.class.isAssignableFrom(nestedPropertyClass)) {
//                    ((AbstractPropertiesConfigs) jbstProperty.getPropertyValue()).assertProperties();
//                } /* else if (AbstractPropertyConfigs.class.isAssignableFrom(nestedPropertyClass)) {
//                    ((AbstractPropertyConfigs) jbstProperty.getPropertyValue()).assertProperties(jbstProperty.getTreePropertyName());
//                } */ else {
//                    jbstProperty.assertOrThrow();
//                }
            } catch (IllegalAccessException ex) {
                throw new IllegalArgumentException(ex);
            }
        });
        if (this.isParent()) {
            this.printProperties();
        }
    }

    private void assertProperties(String parentPropertyName) {
        if (this.isLeaf()) {
            var fields = this.isToggle() ?
                    getMandatoryToggleFields(this, parentPropertyName) :
                    getMandatoryFields(this, parentPropertyName);
            fields.forEach(field -> {
                try {
                    var jbstProperty = new JbstPropertyEdge(parentPropertyName, field, field.get(this));
                    assertNonNullOrThrow(jbstProperty);
                    jbstProperty.assertOrThrow();
                } catch (IllegalAccessException ex) {
                    throw new IllegalArgumentException(ex);
                }
            });
        }
    }

    private void printProperties() {
        if (!this.isParent()) {
            return;
        }
        getMandatoryBasedFields(this, this.getNameNonMandatory()).forEach(field -> {
            try {
                var jbstProperty = new JbstPropertyEdge(this, field, field.get(this));
                if (isNull(jbstProperty.getPropertyValue())) {
                    jbstProperty.print();
                } else {
//                    var nestedPropertyClass = jbstProperty.getPropertyValue().getClass();
//                    if (AbstractPropertiesConfigs.class.isAssignableFrom(nestedPropertyClass)) {
//                        ((AbstractPropertiesConfigs) jbstProperty.getPropertyValue()).printProperties();
//                    } /* else if (AbstractPropertyConfigs.class.isAssignableFrom(nestedPropertyClass)) {
//                        jbstProperty.printAbstractPropertyConfigs();
//                    } */ else {
//                        jbstProperty.print();
//                    }
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
