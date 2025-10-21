package jbst.foundation.domain.reflections;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Comparator;

import static java.util.Objects.nonNull;
import static jbst.foundation.domain.constants.JbstConstants.Logs.PREFIX;
import static jbst.foundation.utilities.strings.StringUtility.toKebab;

@Slf4j
@Data
public class JbstProperty {
    private static final String READABLE_PROPERTY = "%s: `%s`";

    public static final Comparator<JbstProperty> PRINTER_COMPARATOR = (o1, o2) -> {
        if ("enabled".equals(o1.getPropertyName())) {
            return -1;
        } else if ("enabled".equals(o2.getPropertyName())) {
            return 1;
        }
        return o1.getReadableValue().compareTo(o2.getReadableValue());
    };

    private final Field field;
    private final String propertyName;
    private final String treePropertyName;
    private final Object propertyValue;
    private final String readableValue;

    public JbstProperty(@NotNull String propertyName, @NotNull Field field, Object propertyValue) {
        this.field = field;
        this.propertyName = field.getName();
        this.treePropertyName = toKebab(propertyName) + "." + toKebab(this.propertyName);
        this.propertyValue = propertyValue;

        // supports only String[] and ZoneId (on 5+ cases refactoring or extraction required)
        var isArray = nonNull(this.propertyValue) && this.propertyValue.getClass().isArray();
        boolean isArrayOfStrings;
        if (isArray) {
            var array = (Object[]) this.propertyValue;
            isArrayOfStrings = array[0] instanceof String;
        } else {
            isArrayOfStrings = false;
        }
        var isZoneId = nonNull(this.propertyValue) && this.propertyValue instanceof ZoneId;

        if (isArrayOfStrings) {
            var castedPropertyValue = (String[]) this.propertyValue;
            this.readableValue = READABLE_PROPERTY.formatted(this.treePropertyName, Arrays.toString(castedPropertyValue));
        } else if (isZoneId) {
            var castedPropertyValue = (ZoneId) this.propertyValue;
            this.readableValue = READABLE_PROPERTY.formatted(this.treePropertyName, castedPropertyValue.getId());
        } else {
            this.readableValue = READABLE_PROPERTY.formatted(this.treePropertyName, this.propertyValue);
        }
    }

    public void print() {
        LOGGER.debug("{} — {}", PREFIX, this.readableValue);
    }
}
