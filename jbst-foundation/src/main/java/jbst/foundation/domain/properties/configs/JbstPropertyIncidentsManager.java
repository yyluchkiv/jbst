package jbst.foundation.domain.properties.configs;

import jbst.foundation.domain.annotations.JbstModificationBeta;
import jbst.foundation.domain.enums.JbstIncidentsManagerType;
import jbst.foundation.domain.properties.JbstProperty;
import jbst.foundation.domain.properties.annotations.JbstPropertyMandatory;
import jbst.foundation.domain.properties.annotations.JbstPropertyMandatoryOnToggleEnabled;
import jbst.foundation.domain.properties.annotations.JbstPropertyMetadataMapMinSize;
import jbst.foundation.domain.properties.annotations.JbstPropertyOptional;
import jbst.foundation.domain.properties.base.JbstPropertyRemoteServer;
import jbst.foundation.domain.properties.base.JbstPropertyTelegram;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.boot.context.properties.bind.ConstructorBinding;

import java.util.Map;
import java.util.Set;

import static java.lang.Boolean.TRUE;
import static java.util.Objects.nonNull;
import static jbst.foundation.domain.asserts.JbstAsserts.assertTrueOrThrow;
import static jbst.foundation.domain.collections.JbstCollections.baseJoiningRaw;
import static jbst.foundation.domain.constants.JbstConstants.JColor.RED_TEXT;
import static org.apache.commons.collections4.SetUtils.disjunction;

// Lombok (property-based)
@AllArgsConstructor(onConstructor = @__({@ConstructorBinding}))
@Data
@EqualsAndHashCode(callSuper = true)
public class JbstPropertyIncidentsManager extends JbstProperty {
    @JbstPropertyMandatory
    private final boolean enabled;
    @JbstPropertyMandatoryOnToggleEnabled
    private JbstIncidentsManagerType type;
    @JbstPropertyOptional
    private JbstPropertyRemoteServer remoteServer;
    @JbstPropertyOptional
    private JbstPropertyTelegram telegram;
    @JbstPropertyOptional
    @JbstPropertyMetadataMapMinSize(minSize = 0)
    private final Map<String, Boolean> incidents;

    public static JbstPropertyIncidentsManager fixed() {
        return new JbstPropertyIncidentsManager(
                true,
                JbstIncidentsManagerType.fixed(),
                JbstPropertyRemoteServer.fixed(),
                JbstPropertyTelegram.fixed(),
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

    public static JbstPropertyIncidentsManager enabled() {
        return fixed();
    }

    public static JbstPropertyIncidentsManager disabled() {
        return new JbstPropertyIncidentsManager(false, null, null, null, null);
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
                        RED_TEXT.format(baseJoiningRaw(disjunction(this.incidents.keySet(), keys)))
                )
        );
    }

    public <E extends Enum<E>> boolean isEnabled(String type, Class<E> enumClass) {
        if (nonNull(this.incidents)) {
            try {
                E enumValue = Enum.valueOf(enumClass, type);
                return TRUE.equals(this.incidents.get(enumValue.name()));
            } catch (IllegalArgumentException ex) {
                // fallback: type not found in enum
                return false;
            }
        } else {
            return false;
        }
    }
}
