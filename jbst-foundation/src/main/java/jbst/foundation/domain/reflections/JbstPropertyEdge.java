package jbst.foundation.domain.reflections;

import jbst.foundation.domain.asserts.ConsoleAsserts;
import jbst.foundation.domain.properties.JbstProperty;
import jbst.foundation.domain.properties.annotations.MandatoryMapProperty;
import jbst.foundation.utilities.enums.EnumUtility;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static jbst.foundation.domain.asserts.Asserts.assertTrueOrThrow;
import static jbst.foundation.domain.constants.JbstConstants.JColor.BLACK_BOLD_TEXT;
import static jbst.foundation.domain.constants.JbstConstants.JColor.RED_TEXT;
import static jbst.foundation.domain.constants.JbstConstants.Logs.PREFIX;
import static jbst.foundation.domain.reflections.JbstPropertiesUtility.getMandatoryBasedFields;
import static jbst.foundation.utilities.collections.CollectionUtility.baseJoiningRaw;
import static jbst.foundation.utilities.enums.EnumUtility.baseJoining;
import static jbst.foundation.utilities.enums.EnumUtility.baseJoiningWildcard;
import static jbst.foundation.utilities.strings.StringUtility.toKebab;
import static org.apache.commons.collections4.SetUtils.disjunction;

@Slf4j
@Data
public class JbstPropertyEdge {
    public static final Comparator<JbstPropertyEdge> PRINTER_COMPARATOR = (o1, o2) -> {
        if ("enabled".equals(o1.getChild().getName())) {
            return -1;
        } else if ("enabled".equals(o2.getChild().getName())) {
            return 1;
        }
        return o1.getReadable().compareTo(o2.getReadable());
    };

    @NotNull
    private final JbstProperty parent;
    @NotNull
    private final Field child;
    @NotNull
    private final String name;
    @Nullable
    private final Object valueRAW;
    @NotNull
    private final String readable;

    @SneakyThrows
    public JbstPropertyEdge(@NotNull JbstProperty parent, @NotNull Field child) {
        this.parent = parent;
        this.child = child;
        this.name = toKebab(this.parent.getNameNonMandatory()) + "." + toKebab(child.getName());
        this.valueRAW = child.get(parent);

        // supports only String[] and ZoneId (on 5+ cases refactoring or extraction required)
        var isArray = nonNull(this.valueRAW) && this.valueRAW.getClass().isArray();
        boolean isArrayOfStrings;
        if (isArray) {
            var array = (Object[]) this.valueRAW;
            isArrayOfStrings = array[0] instanceof String;
        } else {
            isArrayOfStrings = false;
        }
        var isZoneId = nonNull(this.valueRAW) && this.valueRAW instanceof ZoneId;

        if (isArrayOfStrings) {
            this.readable = Arrays.toString((String[]) this.valueRAW);
        } else if (isZoneId) {
            this.readable = ((ZoneId) this.valueRAW).getId();
        } else if (isNull(this.valueRAW)) {
            this.readable = "null";
        } else {
            this.readable = this.valueRAW.toString();
        }
    }

    public boolean isChildLeaf() {
        return true;
    }

    public boolean isChildBranch() {
        return true;
    }

    public JbstProperty getChildAsJbstProperty() {
        return (JbstProperty) this.valueRAW;
    }

    @SuppressWarnings({"rawtypes", "DataFlowIssue"})
    public void assertOrThrow() {
        if (this.child.isAnnotationPresent(MandatoryMapProperty.class)) {
            var annotation = this.child.getAnnotation(MandatoryMapProperty.class);
            Class<? extends Enum<?>> keySetClass = annotation.keySetClass();
            var castedProperty = (Map) this.valueRAW;
            var size = (annotation.size() == -1) ? keySetClass.getEnumConstants().length : annotation.size();
            //noinspection unchecked
            assertTrueOrThrow(
                    castedProperty.size() == size,
                    "Property %s is invalid. Options: [%s]. Required: [%s]. Disjunction: [%s]".formatted(
                            this.getName(),
                            baseJoiningWildcard(keySetClass),
                            baseJoiningRaw(castedProperty.keySet()),
                            RED_TEXT.format(baseJoining(disjunction(castedProperty.keySet(), EnumUtility.setWildcard(keySetClass))))
                    )
            );
        }
        ConsoleAsserts.PROPERTIES_ACTIONS.entrySet().stream()
                .filter(entry -> entry.getKey().apply(this.valueRAW.getClass()))
                .map(Map.Entry::getValue)
                .findFirst()
                .ifPresent(consumer -> consumer.accept(this));
    }

    public void printAbstractPropertyConfigs() {
        if (!this.isChildLeaf()) {
            return;
        }
        // TODO [YYL] fix assert
        var fields = getMandatoryBasedFields(this, this.name);
        var jbstProperties = getProperties(this.getChildAsJbstProperty(), this.name, fields);
//        jbstProperties.sort(JbstProperty.PRINTER_COMPARATOR);
//        jbstProperties.forEach(JbstProperty::print);
    }

    public static List<JbstPropertyEdge> getProperties(JbstProperty property, String propertyName, List<Field> fields) {
        return fields.stream()
                .map(field -> {
                    try {
//                        return new JbstProperty(propertyName, field, field.get(property));
                        return new JbstPropertyEdge(property, field, field.get(property));
                    } catch (IllegalAccessException | RuntimeException ex) {
//                        return new JbstProperty(propertyName, field, null);
                        return new JbstPropertyEdge(property, field, null);
                    }
                })
                .collect(Collectors.toList());
    }

    public void print() {
        LOGGER.debug("{} — {}: {}", PREFIX, this.name, BLACK_BOLD_TEXT.format(this.readable));
    }
}
