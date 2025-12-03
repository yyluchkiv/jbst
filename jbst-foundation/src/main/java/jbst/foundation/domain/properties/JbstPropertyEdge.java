package jbst.foundation.domain.properties;

import jbst.foundation.domain.asserts.ConsoleAsserts;
import jbst.foundation.domain.properties.annotations.JbstPropertyMetadataMapEnums;
import jbst.foundation.domain.properties.annotations.JbstPropertyMetadataMapMinSize;
import jbst.foundation.domain.enums.JbstEnums;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Map;
import java.util.Objects;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static jbst.foundation.domain.asserts.Asserts.assertTrueOrThrow;
import static jbst.foundation.domain.constants.JbstConstants.JColor.BLACK_BOLD_TEXT;
import static jbst.foundation.domain.constants.JbstConstants.JColor.RED_TEXT;
import static jbst.foundation.domain.constants.JbstConstants.Logs.PREFIX;
import static jbst.foundation.domain.properties.JbstPropertiesUtility.getMandatoryBasedFields;
import static jbst.foundation.domain.collections.JbstCollections.baseJoiningRaw;
import static jbst.foundation.domain.enums.JbstEnums.baseJoining;
import static jbst.foundation.domain.enums.JbstEnums.baseJoiningWildcard;
import static jbst.foundation.domain.strings.JbstStrings.toKebab;
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
    private final Field child;
    @NotNull
    private final String name;
    @Nullable
    private final Object valueRAW;
    @NotNull
    private final String readable;

    @SneakyThrows
    public JbstPropertyEdge(@NotNull String treeName, @NotNull JbstProperty parent, @NotNull Field child) {
        this.child = child;
        this.name = treeName + "." + toKebab(child.getName());
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

    public boolean isChildBranch() {
        return this.valueRAW instanceof JbstProperty property && property.getNodeType().isBranch();
    }

    public boolean isChildLeaf() {
        return this.valueRAW instanceof JbstProperty property && property.getNodeType().isLeaf();
    }

    public JbstProperty getChildAsJbstProperty() {
        return (JbstProperty) this.valueRAW;
    }

    @SuppressWarnings({"rawtypes", "DataFlowIssue"})
    public void assertOrThrow() {
        if (this.child.isAnnotationPresent(JbstPropertyMetadataMapEnums.class)) {
            var annotation = this.child.getAnnotation(JbstPropertyMetadataMapEnums.class);
            Class<? extends Enum<?>> keySetClass = annotation.keySetClass();
            var castedProperty = (Map) this.valueRAW;
            var size = keySetClass.getEnumConstants().length;
            //noinspection unchecked
            assertTrueOrThrow(
                    castedProperty.size() == size,
                    "Property %s is invalid. Options: [%s]. Required: [%s]. Disjunction: [%s]".formatted(
                            this.name,
                            baseJoiningWildcard(keySetClass),
                            baseJoiningRaw(castedProperty.keySet()),
                            RED_TEXT.format(baseJoining(disjunction(castedProperty.keySet(), JbstEnums.setWildcard(keySetClass))))
                    )
            );
        }
        if (this.child.isAnnotationPresent(JbstPropertyMetadataMapMinSize.class)) {
            var annotation = this.child.getAnnotation(JbstPropertyMetadataMapMinSize.class);
            if (annotation.minSize() > 0) {
                var castedProperty = (Map) this.valueRAW;
                assertTrueOrThrow(
                        castedProperty.size() >= annotation.minSize(),
                        "Property %s is invalid. Entries: [%s]. Size: %s. MinSize: %s".formatted(
                                this.name,
                                baseJoiningRaw(castedProperty.entrySet()),
                                castedProperty.size(),
                                annotation.minSize()
                        )
                );
            }
        }
        ConsoleAsserts.PROPERTIES_ACTIONS.entrySet().stream()
                .filter(entry -> entry.getKey().apply(this.valueRAW.getClass()))
                .map(Map.Entry::getValue)
                .findFirst()
                .ifPresent(consumer -> consumer.accept(this));
    }

    public void printChildProperty(String parentTreeName) {
        if (!this.isChildLeaf()) {
            return;
        }
        getMandatoryBasedFields(this.getChildAsJbstProperty(), this.name).stream()
                .map(field -> {
                    try {
                        return new JbstPropertyEdge(parentTreeName, this.getChildAsJbstProperty(), field);
                    } catch (RuntimeException ex) {
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .sorted(JbstPropertyEdge.PRINTER_COMPARATOR)
                .forEach(JbstPropertyEdge::print);
    }

    public void print() {
        LOGGER.debug("{} — {}: {}", PREFIX, this.name, BLACK_BOLD_TEXT.format(this.readable));
    }
}
