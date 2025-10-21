package jbst.foundation.domain.properties.configs;

import jbst.foundation.domain.base.PropertyId;
import jbst.foundation.domain.properties.base.AbstractPropertyConfigs;
import jbst.foundation.domain.reflections.ReflectionProperty;

import static java.util.Objects.isNull;
import static jbst.foundation.domain.properties.utilities.PropertiesAsserter.assertMandatoryPropertiesConfigs;
import static jbst.foundation.domain.properties.utilities.PropertiesAsserter.getMandatoryBasedFields;

public abstract class AbstractPropertiesConfigs {
    public abstract boolean isParentPropertiesNode();
    public abstract PropertyId getPropertyName();

    public void assertProperties() {
        assertMandatoryPropertiesConfigs(this);
        if (this.isParentPropertiesNode()) {
            this.printProperties();
        }
    }

    public void printProperties() {
        getMandatoryBasedFields(this, this.getPropertyName()).forEach(field -> {
            try {
                var rf = new ReflectionProperty(this.getPropertyName(), field, field.get(this));
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
