package jbst.foundation.domain.properties.configs;

import jbst.foundation.domain.properties.utilities.PropertiesAsserter;

public abstract class AbstractTogglePropertiesConfigs extends AbstractPropertiesConfigs {
    public abstract boolean isEnabled();

    @Override
    public void assertProperties() {
        if (this.isEnabled()) {
            PropertiesAsserter.assertMandatoryTogglePropertiesConfigs(this);
        } else {
            PropertiesAsserter.assertMandatoryPropertiesConfigs(this);
        }
        if (this.isParentPropertiesNode()) {
            this.printProperties();
        }
    }
}
