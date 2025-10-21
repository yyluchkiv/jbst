package jbst.foundation.domain.properties.base;

import jbst.foundation.domain.reflections.JbstPropertiesUtility;

public abstract class AbstractTogglePropertyConfigs extends AbstractPropertyConfigs {
    protected abstract boolean isEnabled();

    @Override
    public void assertProperties(String propertyName) {
        if (this.isEnabled()) {
            this.assertFields(propertyName, JbstPropertiesUtility.getMandatoryToggleFields(this, propertyName));
        } else {
            this.assertFields(propertyName, JbstPropertiesUtility.getMandatoryFields(this, propertyName));
        }
    }
}
