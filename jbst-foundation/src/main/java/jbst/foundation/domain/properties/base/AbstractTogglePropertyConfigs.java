package jbst.foundation.domain.properties.base;

import static jbst.foundation.domain.properties.utilities.PropertiesAsserter.getMandatoryFields;
import static jbst.foundation.domain.properties.utilities.PropertiesAsserter.getMandatoryToggleFields;

public abstract class AbstractTogglePropertyConfigs extends AbstractPropertyConfigs {
    protected abstract boolean isEnabled();

    @Override
    public void assertProperties(String propertyName) {
        if (this.isEnabled()) {
            this.assertFields(propertyName, getMandatoryToggleFields(this, propertyName));
        } else {
            this.assertFields(propertyName, getMandatoryFields(this, propertyName));
        }
    }
}
