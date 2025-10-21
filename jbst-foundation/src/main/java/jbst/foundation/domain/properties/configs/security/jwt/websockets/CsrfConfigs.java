package jbst.foundation.domain.properties.configs.security.jwt.websockets;

import jbst.foundation.domain.constants.JbstConstants;
import jbst.foundation.domain.properties.AbstractProperty;
import jbst.foundation.domain.properties.annotations.MandatoryProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.boot.context.properties.bind.ConstructorBinding;

import static jbst.foundation.utilities.random.RandomUtility.randomString;

// Lombok (property-based)
@AllArgsConstructor(onConstructor = @__({@ConstructorBinding}))
@Data
@EqualsAndHashCode(callSuper = true)
public class CsrfConfigs extends AbstractProperty {
    @MandatoryProperty
    private final String headerName;
    @MandatoryProperty
    private final String parameterName;
    @MandatoryProperty
    private final String tokenKey;

    public static CsrfConfigs hardcoded() {
        return new CsrfConfigs("csrf-header", "_csrf", "csrf-token-key");
    }

    public static CsrfConfigs random() {
        return new CsrfConfigs(randomString(), randomString(), randomString());
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
}
