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
            this.printProperties();
        }
    }

    public void printProperties() {
        var fields = PropertiesAsserter.getMandatoryBasedFields(this, this.getPropertyId());
        fields.forEach(field -> {
            try {
                var rf = new ReflectionProperty(this.getPropertyId(), field, field.get(this));
                if (isNull(rf.getPropertyValue())) {
                    rf.print();
                } else {
                    var nestedPropertyClass = rf.getPropertyValue().getClass();
                    if (AbstractPropertiesConfigs.class.isAssignableFrom(nestedPropertyClass)) {
                        ((AbstractPropertiesConfigs) rf.getPropertyValue()).printProperties();
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
