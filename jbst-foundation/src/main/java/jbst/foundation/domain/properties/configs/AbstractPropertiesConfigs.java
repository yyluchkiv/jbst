package jbst.foundation.domain.properties.configs;

import jbst.foundation.domain.base.PropertyId;
import jbst.foundation.domain.properties.base.AbstractPropertyConfigs;
import jbst.foundation.domain.properties.utilities.PropertiesAsserter;
import jbst.foundation.domain.reflections.ReflectionProperty;

import static java.util.Objects.isNull;

public abstract class AbstractPropertiesConfigs {
    public abstract boolean isParentPropertiesNode();
    public abstract PropertyId getPropertyId();

    public void assertProperties() {
        PropertiesAsserter.assertMandatoryPropertiesConfigs(this, this.getPropertyId());
        if (this.isParentPropertiesNode()) {
            printProperties(this.getPropertyId());
        }
    }

    @Deprecated
    public void assertProperties(PropertyId propertyId) {
        PropertiesAsserter.assertMandatoryPropertiesConfigs(this, propertyId);
        if (this.isParentPropertiesNode()) {
            printProperties(propertyId);
        }
    }

    @Deprecated
    public void printProperties(PropertyId propertyId) {
        this.printMandatoryPropertiesConfigs(propertyId);
    }

    // =================================================================================================================
    // PRIVATE METHODS
    // =================================================================================================================
    @Deprecated
    private void printMandatoryPropertiesConfigs(PropertyId propertyId) {
        var fields = PropertiesAsserter.getMandatoryBasedFields(this, propertyId);
        fields.forEach(field -> {
            try {
                var rf = new ReflectionProperty(propertyId, field, field.get(this));
                if (isNull(rf.getPropertyValue())) {
                    rf.print();
                } else {
                    var nestedPropertyClass = rf.getPropertyValue().getClass();
                    if (AbstractPropertiesConfigs.class.isAssignableFrom(nestedPropertyClass)) {
                        ((AbstractPropertiesConfigs) rf.getPropertyValue()).printProperties(rf.getTreePropertyId());
                    } else if (AbstractPropertyConfigs.class.isAssignableFrom(nestedPropertyClass)) {
                        ((AbstractPropertyConfigs) rf.getPropertyValue()).printProperties(rf.getTreePropertyId());
                    } else {
                        rf.print();
                    }
                }
            } catch (IllegalAccessException ex) {
                throw new IllegalArgumentException(ex);
            }
        });
    }
}
