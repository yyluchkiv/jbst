package jbst.foundation.domain.properties.base;

import jbst.foundation.domain.constants.JbstConstants;
import jbst.foundation.domain.properties.JbstProperty;
import jbst.foundation.domain.properties.annotations.JbstPropertyMandatory;
import jbst.foundation.domain.properties.annotations.JbstPropertyMandatoryOnToggleEnabled;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.boot.context.properties.bind.ConstructorBinding;

import static jbst.foundation.domain.constants.JbstConstants.ZoneIds.UKRAINE;
import static jbst.foundation.domain.random.JbstRandom.randomBoolean;

@AllArgsConstructor(onConstructor = @__({@ConstructorBinding}))
@Data
@EqualsAndHashCode(callSuper = true)
public class JbstPropertyCron extends JbstProperty {
    @JbstPropertyMandatory
    private final boolean enabled;
    @JbstPropertyMandatoryOnToggleEnabled
    private String expression;
    @JbstPropertyMandatoryOnToggleEnabled
    private String zoneId;

    public static JbstPropertyCron fixed() {
        return new JbstPropertyCron(true, "*/30 * * * * *", UKRAINE.getId());
    }

    public static JbstPropertyCron enabled(String expression, String zoneId) {
        return new JbstPropertyCron(true, expression, zoneId);
    }

    public static JbstPropertyCron enabled() {
        return fixed();
    }

    public static JbstPropertyCron disabled() {
        return new JbstPropertyCron(false, null, null);
    }

    public static JbstPropertyCron random() {
        return randomBoolean() ? enabled() : disabled();
    }

    @Override
    public JbstPropertyNodeType getNodeType() {
        return JbstPropertyNodeType.LEAF;
    }

    @Override
    public boolean isToggle() {
        return this.enabled;
    }

    @Override
    public String getNameNonLeaf() {
        return JbstConstants.Symbols.DASH;
    }
}
