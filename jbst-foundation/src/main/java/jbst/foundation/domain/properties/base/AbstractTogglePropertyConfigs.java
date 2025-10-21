package jbst.foundation.domain.properties.base;

import static jbst.foundation.domain.properties.utilities.PropertiesAsserter.assertMandatoryPropertyConfigs;
import static jbst.foundation.domain.properties.utilities.PropertiesAsserter.assertMandatoryTogglePropertyConfigs;

public abstract class AbstractTogglePropertyConfigs extends AbstractPropertyConfigs {
    protected abstract boolean isEnabled();

    @Override
    public void assertProperties(String propertyName) {
        if (this.isEnabled()) {
            assertMandatoryTogglePropertyConfigs(this, propertyName);
        } else {
            assertMandatoryPropertyConfigs(this, propertyName);
        }
    }
}
