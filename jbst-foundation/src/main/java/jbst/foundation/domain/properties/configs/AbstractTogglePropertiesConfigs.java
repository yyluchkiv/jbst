package jbst.foundation.domain.properties.configs;

import jbst.foundation.domain.reflections.JbstPropertiesUtility;

public abstract class AbstractTogglePropertiesConfigs extends AbstractPropertiesConfigs {
    public abstract boolean isEnabled();

    @Override
    public void assertProperties() {
        if (this.isEnabled()) {
            this.assertFields(JbstPropertiesUtility.getMandatoryToggleFields(this, this.getPropertyName()));
        } else {
            this.assertFields(JbstPropertiesUtility.getMandatoryFields(this, this.getPropertyName()));
        }
        if (this.isParentPropertiesNode()) {
            this.printProperties();
        }
    }
}
