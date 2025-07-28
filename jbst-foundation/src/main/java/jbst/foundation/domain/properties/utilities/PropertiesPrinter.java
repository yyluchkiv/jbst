package jbst.foundation.domain.properties.utilities;

import jbst.foundation.domain.base.PropertyId;
import jbst.foundation.domain.constants.JbstConstants;
import jbst.foundation.domain.properties.base.AbstractPropertyConfigs;
import jbst.foundation.domain.properties.configs.AbstractPropertiesConfigs;
import jbst.foundation.domain.reflections.ReflectionProperty;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import static java.util.Objects.isNull;
import static jbst.foundation.utilities.reflections.ReflectionUtility.getProperties;

@Slf4j
@UtilityClass
public class PropertiesPrinter {

    public static void printProperty(ReflectionProperty rf) {
        LOGGER.debug(JbstConstants.Logs.PREFIX + " — {}", rf.getReadableValue());
    }

    public static void printMandatoryPropertiesConfigs(AbstractPropertiesConfigs propertiesConfigs, PropertyId propertyId) {
        var fields = PropertiesAsserter.getMandatoryBasedFields(propertiesConfigs, propertyId);
        fields.forEach(field -> {
            try {
                var rf = new ReflectionProperty(propertyId, field, field.get(propertiesConfigs));
                if (isNull(rf.getPropertyValue())) {
                    printProperty(rf);
                } else {
                    var nestedPropertyClass = rf.getPropertyValue().getClass();
                    if (AbstractPropertiesConfigs.class.isAssignableFrom(nestedPropertyClass)) {
                        ((AbstractPropertiesConfigs) rf.getPropertyValue()).printProperties(rf.getTreePropertyId());
                    } else if (AbstractPropertyConfigs.class.isAssignableFrom(nestedPropertyClass)) {
                        ((AbstractPropertyConfigs) rf.getPropertyValue()).printProperties(rf.getTreePropertyId());
                    } else {
                        printProperty(rf);
                    }
                }
            } catch (IllegalAccessException ex) {
                throw new IllegalArgumentException(ex);
            }
        });
    }

    public static void printMandatoryBasedConfigs(AbstractPropertyConfigs propertyConfigs, PropertyId propertyId) {
        var fields = PropertiesAsserter.getMandatoryBasedFields(propertyConfigs, propertyId);
        var rfs = getProperties(propertyConfigs, propertyId, fields);
        rfs.sort(ReflectionProperty.PRINTER_COMPARATOR);
        rfs.forEach(PropertiesPrinter::printProperty);
    }
}
