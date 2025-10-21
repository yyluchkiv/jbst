package jbst.foundation.domain.properties.configs.security.jwt;

import jbst.foundation.domain.constants.JbstConstants;
import jbst.foundation.domain.properties.annotations.AbstractProperty;
import jbst.foundation.domain.properties.annotations.MandatoryMapProperty;
import jbst.foundation.domain.properties.annotations.MandatoryProperty;
import jbst.foundation.domain.properties.base.JbstIamIncidentType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.boot.context.properties.bind.ConstructorBinding;

import java.util.EnumMap;
import java.util.Map;

import static java.lang.Boolean.TRUE;
import static jbst.foundation.domain.properties.base.JbstIamIncidentType.*;
import static jbst.foundation.utilities.random.RandomUtility.getEnumMapMappedRandomBoolean;

// Lombok (property-based)
@AllArgsConstructor(onConstructor = @__({@ConstructorBinding}))
@Data
@EqualsAndHashCode(callSuper = true)
public class IncidentsConfigs extends AbstractProperty {
    @MandatoryProperty
    @MandatoryMapProperty(propertyName = "typesConfigs", keySetClass = JbstIamIncidentType.class)
    private final Map<JbstIamIncidentType, Boolean> typesConfigs;

    public static IncidentsConfigs hardcoded() {
        return new IncidentsConfigs(
                new EnumMap<>(
                        Map.ofEntries(
                                Map.entry(AUTHENTICATION_LOGIN, true),
                                Map.entry(AUTHENTICATION_LOGIN_FAILURE_USERNAME_PASSWORD, false),
                                Map.entry(AUTHENTICATION_LOGIN_FAILURE_USERNAME_MASKED_PASSWORD, true),
                                Map.entry(AUTHENTICATION_LOGOUT, false),
                                Map.entry(AUTHENTICATION_LOGOUT_MIN, false),
                                Map.entry(SESSION_REFRESHED, true),
                                Map.entry(SESSION_EXPIRED, false),
                                Map.entry(REGISTER_MAGICLINK, true),
                                Map.entry(REGISTER0, true),
                                Map.entry(REGISTER0_FAILURE, true),
                                Map.entry(REGISTER1, true),
                                Map.entry(REGISTER1_FAILURE, true)
                        )
                )
        );
    }

    public static IncidentsConfigs random() {
        return new IncidentsConfigs(getEnumMapMappedRandomBoolean(JbstIamIncidentType.values()));
    }

    @Override
    public boolean isParent() {
        return false;
    }

    @Override
    public boolean isLeaf() {
        return true;
    }

    @Override
    public boolean isToggle() {
        return false;
    }

    @Override
    public String getNameNonMandatory() {
        return JbstConstants.Symbols.DASH;
    }

    @Override
    public void assertProperties(String propertyName) {
        super.assertProperties(propertyName);
        if (TRUE.equals(this.typesConfigs.get(AUTHENTICATION_LOGIN_FAILURE_USERNAME_PASSWORD)) &&
                TRUE.equals(this.typesConfigs.get(AUTHENTICATION_LOGIN_FAILURE_USERNAME_MASKED_PASSWORD))) {
            throw new IllegalArgumentException("Please configure login failure incident feature. Only one feature type could be enabled");
        }
    }

    public boolean isEnabled(JbstIamIncidentType type) {
        return TRUE.equals(this.typesConfigs.get(type));
    }
}
