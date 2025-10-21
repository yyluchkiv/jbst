package jbst.foundation.domain.properties.base;

import jbst.foundation.domain.base.PropertyId;
import jbst.foundation.domain.reflections.ReflectionProperty;
import lombok.extern.slf4j.Slf4j;

import static jbst.foundation.domain.properties.utilities.PropertiesAsserter.assertMandatoryPropertyConfigs;
import static jbst.foundation.domain.properties.utilities.PropertiesAsserter.getMandatoryBasedFields;
import static jbst.foundation.utilities.reflections.ReflectionUtility.getProperties;

// TODO [YYL] merge AbstractPropertyConfigs + AbstractPropertiesConfigs
@Slf4j
public abstract class AbstractPropertyConfigs {

    public void assertProperties(PropertyId propertyId) {
        assertMandatoryPropertyConfigs(this, propertyId);
    }

    public void printProperties(PropertyId propertyId) {
        var fields = getMandatoryBasedFields(this, propertyId);
        var rfs = getProperties(this, propertyId, fields);
        rfs.sort(ReflectionProperty.PRINTER_COMPARATOR);
        rfs.forEach(ReflectionProperty::print);
    }
}
