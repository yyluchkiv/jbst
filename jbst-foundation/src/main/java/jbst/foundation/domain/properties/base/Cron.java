package jbst.foundation.domain.properties.base;

import jbst.foundation.domain.constants.JbstConstants;
import jbst.foundation.domain.properties.annotations.AbstractProperty;
import jbst.foundation.domain.properties.annotations.MandatoryProperty;
import jbst.foundation.domain.properties.annotations.MandatoryToggleProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.boot.context.properties.bind.ConstructorBinding;

import static jbst.foundation.domain.constants.JbstConstants.ZoneIds.UKRAINE;
import static jbst.foundation.utilities.random.RandomUtility.randomBoolean;

@AllArgsConstructor(onConstructor = @__({@ConstructorBinding}))
@Data
@EqualsAndHashCode(callSuper = true)
public class Cron extends AbstractProperty {
    @MandatoryProperty
    private final boolean enabled;
    @MandatoryToggleProperty
    private String expression;
    @MandatoryToggleProperty
    private String zoneId;

    public static Cron hardcoded() {
        return new Cron(true, "*/30 * * * * *", UKRAINE.getId());
    }

    public static Cron enabled(String expression, String zoneId) {
        return new Cron(true, expression, zoneId);
    }

    public static Cron enabled() {
        return hardcoded();
    }

    public static Cron disabled() {
        return new Cron(false, null, null);
    }

    public static Cron random() {
        return randomBoolean() ? enabled() : disabled();
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
        return true;
    }

    @Override
    public String getNameNonMandatory() {
        return JbstConstants.Symbols.DASH;
    }
}
