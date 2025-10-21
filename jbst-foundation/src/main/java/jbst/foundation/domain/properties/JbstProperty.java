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
public abstract class JbstProperty {
    public abstract boolean isRoot();
    public abstract boolean isLeaf();
    public abstract boolean isToggle();
    @JbstNonMandatoryMethod
    public abstract String getNameNonMandatory();

    // TODO [YYL] fixme?
    public void assertPropertyTree() {
        if (this.isLeaf()) {
            return;
        }
        var fields = this.isToggle() ?
                getMandatoryToggleFields(this, this.getNameNonMandatory()) :
                getMandatoryFields(this, this.getNameNonMandatory());
        fields.forEach(field -> {
            var edge = new JbstPropertyEdge(this, field);
            assertNonNullOrThrow(edge);
            if (edge.isBranch()) {
                edge.getChildAsJbstProperty().assertPropertyTree();
            } else if (edge.isLeaf()) {

            } else {
                edge.assertOrThrow();
            }


            if (edge.getParent().isLeaf()) {
                this.assertProperties(edge.getParent());
            } else {

            }
            var nestedPropertyClass = requireNonNull(edge.getValueRAW()).getClass();
//                if (AbstractPropertiesConfigs.class.isAssignableFrom(nestedPropertyClass)) {
//                    ((AbstractPropertiesConfigs) jbstProperty.getPropertyValue()).assertProperties();
//                } /* else if (AbstractPropertyConfigs.class.isAssignableFrom(nestedPropertyClass)) {
//                    ((AbstractPropertyConfigs) jbstProperty.getPropertyValue()).assertProperties(jbstProperty.getTreePropertyName());
//                } */ else {
//                    jbstProperty.assertOrThrow();
//                }
        });
        if (this.isRoot()) {
            this.printProperties();
        }
    }

    private void assertProperties(JbstProperty parent) {
        if (!this.isLeaf()) {
            return;
        }
        var fields = this.isToggle() ?
                getMandatoryToggleFields(this, parent.getNameNonMandatory()) :
                getMandatoryFields(this, parent.getNameNonMandatory());
        fields.forEach(field -> {
            var edge = new JbstPropertyEdge(parent, field);
            assertNonNullOrThrow(edge);
            edge.assertOrThrow();
        });
    }

    private void printProperties() {
        if (!this.isRoot()) {
            return;
        }
        getMandatoryBasedFields(this, this.getNameNonMandatory()).forEach(field -> {
            var jbstProperty = new JbstPropertyEdge(this, field);
            if (isNull(jbstProperty.getValueRAW())) {
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
