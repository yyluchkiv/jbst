package jbst.foundation.domain.properties.configs;

import jbst.foundation.domain.properties.JbstProperty;
import jbst.foundation.domain.properties.annotations.MandatoryProperty;
import jbst.foundation.domain.properties.annotations.MandatoryPropertyMapMinSize;
import jbst.foundation.domain.properties.annotations.MandatoryToggleProperty;
import jbst.foundation.domain.properties.base.IncidentsManagerType;
import jbst.foundation.domain.properties.base.JbstIamIncidentType;
import jbst.foundation.domain.properties.base.RemoteServer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.boot.context.properties.bind.ConstructorBinding;

import java.util.Arrays;
import java.util.Map;

import static java.lang.Boolean.TRUE;
import static jbst.foundation.domain.asserts.Asserts.assertTrueOrThrow;
import static jbst.foundation.utilities.collections.CollectionUtility.baseJoiningRaw;
import static jbst.foundation.utilities.random.RandomUtility.getEnumMapMappedRandomBoolean;
import static jbst.foundation.utilities.random.RandomUtility.randomBoolean;

// Lombok (property-based)
@AllArgsConstructor(onConstructor = @__({@ConstructorBinding}))
@Data
@EqualsAndHashCode(callSuper = true)
public class IncidentsManagerConfigs extends JbstProperty {
    @MandatoryProperty
    private final boolean enabled;
    @MandatoryToggleProperty
    private IncidentsManagerType type;
    @MandatoryToggleProperty
    private RemoteServer remoteServer;
    @MandatoryToggleProperty
    @MandatoryPropertyMapMinSize(propertyName = "types", minSize = 12)
    private final Map<String, Boolean> types;

    public static IncidentsManagerConfigs hardcoded() {
        return new IncidentsManagerConfigs(
                true,
                IncidentsManagerType.hardcoded(),
                RemoteServer.hardcoded(),
                Map.ofEntries(
                        Map.entry("AUTHENTICATION_LOGIN", true),
                        Map.entry("AUTHENTICATION_LOGIN_FAILURE_USERNAME_PASSWORD", false),
                        Map.entry("AUTHENTICATION_LOGIN_FAILURE_USERNAME_MASKED_PASSWORD", true),
                        Map.entry("AUTHENTICATION_LOGOUT", false),
                        Map.entry("AUTHENTICATION_LOGOUT_MIN", false),
                        Map.entry("SESSION_REFRESHED", true),
                        Map.entry("SESSION_EXPIRED", false),
                        Map.entry("REGISTER_MAGICLINK", true),
                        Map.entry("REGISTER0", true),
                        Map.entry("REGISTER0_FAILURE", true),
                        Map.entry("REGISTER1", true),
                        Map.entry("REGISTER1_FAILURE", true)
                )
        );
    }

    public static IncidentsManagerConfigs random() {
        return new IncidentsManagerConfigs(
                randomBoolean(),
                IncidentsManagerType.random(),
                RemoteServer.random(),
                getEnumMapMappedRandomBoolean(Arrays.stream(JbstIamIncidentType.values()).map(Enum::name).toArray(String[]::new))
        );
    }

    public static IncidentsManagerConfigs enabled() {
        return hardcoded();
    }

    public static IncidentsManagerConfigs disabled() {
        return new IncidentsManagerConfigs(false, null, null, null);
    }

    @Override
    public JbstPropertyNodeType getNodeType() {
        return JbstPropertyNodeType.ROOT;
    }

    @Override
    public boolean isToggle() {
        return this.enabled;
    }

    @Override
    public String getNameNonLeaf() {
        return "incidents-manager-configs";
    }

    public void assertPropertiesExtended(int size) {
        assertTrueOrThrow(
                this.types.size() >= size,
                "Property %s is invalid. Entries: [%s]. MinSize: %s".formatted(
                        this.getNameNonLeaf() + ".types",
                        baseJoiningRaw(this.types.entrySet()),
                        size
                )
        );
    }

    public <E extends Enum<E>> boolean isEnabled(String type, Class<E> enumClass) {
        try {
            E enumValue = Enum.valueOf(enumClass, type);
            return TRUE.equals(this.types.get(enumValue.name()));
        } catch (IllegalArgumentException ex) {
            // fallback: type not found in enum
            return false;
        }
    }
}
