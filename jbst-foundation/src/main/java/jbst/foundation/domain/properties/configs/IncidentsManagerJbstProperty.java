package jbst.foundation.domain.properties.configs;

import jbst.foundation.domain.annotations.JbstModificationBeta;
import jbst.foundation.domain.properties.JbstProperty;
import jbst.foundation.domain.properties.annotations.MandatoryProperty;
import jbst.foundation.domain.properties.annotations.MandatoryPropertyMapMinSize;
import jbst.foundation.domain.properties.annotations.MandatoryPropertyToggle;
import jbst.foundation.domain.enums.JbstIncidentsManagerType;
import jbst.foundation.domain.enums.JbstSecurityJwtIncident;
import jbst.foundation.domain.properties.base.RemoteServer;
import jbst.foundation.utilities.collections.CollectionUtility;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.boot.context.properties.bind.ConstructorBinding;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;

import static java.lang.Boolean.TRUE;
import static jbst.foundation.domain.asserts.Asserts.assertTrueOrThrow;
import static jbst.foundation.domain.constants.JbstConstants.JColor.RED_TEXT;
import static jbst.foundation.utilities.collections.CollectionUtility.baseJoiningRaw;
import static jbst.foundation.utilities.random.RandomUtility.getEnumMapMappedRandomBoolean;
import static jbst.foundation.utilities.random.RandomUtility.randomBoolean;
import static org.apache.commons.collections4.SetUtils.disjunction;

// Lombok (property-based)
@AllArgsConstructor(onConstructor = @__({@ConstructorBinding}))
@Data
@EqualsAndHashCode(callSuper = true)
public class IncidentsManagerJbstProperty extends JbstProperty {
    @MandatoryProperty
    private final boolean enabled;
    @MandatoryPropertyToggle
    private JbstIncidentsManagerType type;
    @MandatoryPropertyToggle
    private RemoteServer remoteServer;
    @MandatoryPropertyToggle
    @MandatoryPropertyMapMinSize(minSize = 0)
    private final Map<String, Boolean> incidents;

    public static IncidentsManagerJbstProperty hardcoded() {
        return new IncidentsManagerJbstProperty(
                true,
                JbstIncidentsManagerType.hardcoded(),
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

    public static IncidentsManagerJbstProperty random() {
        return new IncidentsManagerJbstProperty(
                randomBoolean(),
                JbstIncidentsManagerType.random(),
                RemoteServer.random(),
                getEnumMapMappedRandomBoolean(Arrays.stream(JbstSecurityJwtIncident.values()).map(Enum::name).toArray(String[]::new))
        );
    }

    public static IncidentsManagerJbstProperty enabled() {
        return hardcoded();
    }

    public static IncidentsManagerJbstProperty disabled() {
        return new IncidentsManagerJbstProperty(false, null, null, null);
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
        return "incidents-manager";
    }

    @JbstModificationBeta(releaseVersion = "v1.31")
    public void assertPropertiesExtended(Set<String> keys) {
        assertTrueOrThrow(
                this.incidents.size() >= keys.size(),
                "Property %s is invalid. Options: [%s]. Required: [%s]. Disjunction: [%s]".formatted(
                        "incidents-manager.incidents",
                        baseJoiningRaw(this.incidents.entrySet()),
                        baseJoiningRaw(keys),
                        RED_TEXT.format(CollectionUtility.baseJoiningRaw(disjunction(this.incidents.keySet(), keys)))
                )
        );
    }

    public <E extends Enum<E>> boolean isEnabled(String type, Class<E> enumClass) {
        try {
            E enumValue = Enum.valueOf(enumClass, type);
            return TRUE.equals(this.incidents.get(enumValue.name()));
        } catch (IllegalArgumentException ex) {
            // fallback: type not found in enum
            return false;
        }
    }
}
