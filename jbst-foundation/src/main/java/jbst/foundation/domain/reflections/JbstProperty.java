package jbst.foundation.domain.reflections;

import jbst.foundation.domain.asserts.ConsoleAsserts;
import jbst.foundation.domain.properties.annotations.MandatoryMapProperty;
import jbst.foundation.domain.properties.base.AbstractPropertyConfigs;
import jbst.foundation.utilities.enums.EnumUtility;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Map;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static jbst.foundation.domain.asserts.Asserts.assertTrueOrThrow;
import static jbst.foundation.domain.constants.JbstConstants.JColor.BLACK_BOLD_TEXT;
import static jbst.foundation.domain.constants.JbstConstants.JColor.RED_TEXT;
import static jbst.foundation.domain.constants.JbstConstants.Logs.PREFIX;
import static jbst.foundation.utilities.collections.CollectionUtility.baseJoiningRaw;
import static jbst.foundation.utilities.enums.EnumUtility.baseJoining;
import static jbst.foundation.utilities.enums.EnumUtility.baseJoiningWildcard;
import static jbst.foundation.utilities.strings.StringUtility.toKebab;
import static org.apache.commons.collections4.SetUtils.disjunction;

@Slf4j
@Data
public class JbstProperty {
    public static final Comparator<JbstProperty> PRINTER_COMPARATOR = (o1, o2) -> {
        if ("enabled".equals(o1.getPropertyName())) {
            return -1;
        } else if ("enabled".equals(o2.getPropertyName())) {
            return 1;
        }
        return o1.getReadableValue().compareTo(o2.getReadableValue());
    };

    @NotNull
    private final Field field;
    @NotNull
    private final String propertyName;
    @NotNull
    private final String treePropertyName;
    @Nullable
    private final Object propertyValue;
    @NotNull
    private final String readableValue;

    public JbstProperty(@NotNull String propertyName, @NotNull Field field, @Nullable Object propertyValue) {
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
            this.readableValue = Arrays.toString((String[]) this.propertyValue);
        } else if (isZoneId) {
            this.readableValue = ((ZoneId) this.propertyValue).getId();
        } else if (isNull(this.propertyValue)) {
            this.readableValue = "null";
        } else {
            this.readableValue = this.propertyValue.toString();
        }
    }

    @SuppressWarnings({"rawtypes", "DataFlowIssue"})
    public void verify() {
        if (this.field.isAnnotationPresent(MandatoryMapProperty.class)) {
            var annotation = this.field.getAnnotation(MandatoryMapProperty.class);
            Class<? extends Enum<?>> keySetClass = annotation.keySetClass();
            var castedProperty = (Map) this.propertyValue;
            var size = (annotation.size() == -1) ? keySetClass.getEnumConstants().length : annotation.size();
            //noinspection unchecked
            assertTrueOrThrow(
                    castedProperty.size() == size,
                    "Property %s is invalid. Options: [%s]. Required: [%s]. Disjunction: [%s]".formatted(
                            this.getTreePropertyName(),
                            baseJoiningWildcard(keySetClass),
                            baseJoiningRaw(castedProperty.keySet()),
                            RED_TEXT.format(baseJoining(disjunction(castedProperty.keySet(), EnumUtility.setWildcard(keySetClass))))
                    )
            );
        }
        ConsoleAsserts.PROPERTIES_ACTIONS.entrySet().stream()
                .filter(entry -> entry.getKey().apply(this.propertyValue.getClass()))
                .map(Map.Entry::getValue)
                .findFirst()
                .ifPresent(consumer -> consumer.accept(this));
    }

    public void printAbstractPropertyConfigs() {
        assertTrueOrThrow(nonNull(this.propertyValue) && AbstractPropertyConfigs.class.isAssignableFrom(this.propertyValue.getClass()));
        var fields = JbstPropertiesUtility.getMandatoryBasedFields(this, this.treePropertyName);
        var jbstProperties = JbstPropertiesUtility.getProperties(this, this.treePropertyName, fields);
        jbstProperties.sort(JbstProperty.PRINTER_COMPARATOR);
        jbstProperties.forEach(JbstProperty::print);
    }

    public void print() {
        LOGGER.debug("{} — {}: {}", PREFIX, this.treePropertyName, BLACK_BOLD_TEXT.format(this.readableValue));
    }
}
