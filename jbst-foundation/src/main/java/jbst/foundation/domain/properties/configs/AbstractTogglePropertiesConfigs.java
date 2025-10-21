package jbst.foundation.domain.properties.configs;

import jbst.foundation.domain.properties.utilities.PropertiesAsserter;

public abstract class AbstractTogglePropertiesConfigs extends AbstractPropertiesConfigs {
    public abstract boolean isParentPropertiesNode();
    public abstract boolean isEnabled();

    @Override
    public void assertProperties() {
        if (this.isEnabled()) {
            PropertiesAsserter.assertMandatoryTogglePropertiesConfigs(this, this.getPropertyId());
        } else {
            PropertiesAsserter.assertMandatoryPropertiesConfigs(this, this.getPropertyId());
        }
        if (this.isParentPropertiesNode()) {
            printProperties(this.getPropertyId());
        }
    }
}
