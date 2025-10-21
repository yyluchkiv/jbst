package jbst.foundation.domain.properties.base;

import jbst.foundation.domain.reflections.JbstPropertiesUtility;
import jbst.foundation.domain.reflections.JbstProperty;
import lombok.extern.slf4j.Slf4j;

import static jbst.foundation.domain.properties.utilities.PropertiesAsserter.assertMandatoryPropertyConfigs;
import static jbst.foundation.domain.properties.utilities.PropertiesAsserter.getMandatoryBasedFields;

// TODO [YYL] merge AbstractPropertyConfigs + AbstractPropertiesConfigs
@Slf4j
public abstract class AbstractPropertyConfigs {

    public void assertProperties(String propertyName) {
        assertMandatoryPropertyConfigs(this, propertyName);
    }

    public void printProperties(String propertyName) {
        var fields = getMandatoryBasedFields(this, propertyName);
        var rfs = JbstPropertiesUtility.getProperties(this, propertyName, fields);
        rfs.sort(JbstProperty.PRINTER_COMPARATOR);
        rfs.forEach(JbstProperty::print);
    }
}
