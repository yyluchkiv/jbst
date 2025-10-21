package jbst.foundation.domain.properties.configs;

import static jbst.foundation.domain.properties.utilities.PropertiesAsserter.assertMandatoryPropertiesConfigs;
import static jbst.foundation.domain.properties.utilities.PropertiesAsserter.assertMandatoryTogglePropertiesConfigs;

public abstract class AbstractTogglePropertiesConfigs extends AbstractPropertiesConfigs {
    public abstract boolean isEnabled();

    @Override
    public void assertProperties() {
        if (this.isEnabled()) {
            assertMandatoryTogglePropertiesConfigs(this);
        } else {
            assertMandatoryPropertiesConfigs(this);
        }
        if (this.isParentPropertiesNode()) {
            this.printProperties();
        }
    }
}
