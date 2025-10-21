package jbst.foundation.domain.properties.base;

import jbst.foundation.domain.base.PropertyId;
import jbst.foundation.domain.properties.utilities.PropertiesAsserter;
import jbst.foundation.domain.reflections.ReflectionProperty;
import lombok.extern.slf4j.Slf4j;

import static jbst.foundation.utilities.reflections.ReflectionUtility.getProperties;

@Slf4j
public abstract class AbstractPropertyConfigs {

    @Deprecated
    public void assertProperties(PropertyId propertyId) {
        PropertiesAsserter.assertMandatoryPropertyConfigs(this, propertyId);
    }

    @Deprecated
    public void printProperties(PropertyId propertyId) {
        this.printMandatoryBasedConfigs(propertyId);
    }

    // =================================================================================================================
    // PRIVATE METHODS
    // =================================================================================================================
    private void printMandatoryBasedConfigs(PropertyId propertyId) {
        var fields = PropertiesAsserter.getMandatoryBasedFields(this, propertyId);
        var rfs = getProperties(this, propertyId, fields);
        rfs.sort(ReflectionProperty.PRINTER_COMPARATOR);
        rfs.forEach(ReflectionProperty::print);
    }
}
