package jbst.foundation.domain.properties.configs;

import static jbst.foundation.domain.properties.utilities.PropertiesAsserter.getMandatoryFields;
import static jbst.foundation.domain.properties.utilities.PropertiesAsserter.getMandatoryToggleFields;

public abstract class AbstractTogglePropertiesConfigs extends AbstractPropertiesConfigs {
    public abstract boolean isEnabled();

    @Override
    public void assertProperties() {
        if (this.isEnabled()) {
            this.assertFields(getMandatoryToggleFields(this, this.getPropertyName()));
        } else {
            this.assertFields(getMandatoryFields(this, this.getPropertyName()));
        }
        if (this.isParentPropertiesNode()) {
            this.printProperties();
        }
    }
}
