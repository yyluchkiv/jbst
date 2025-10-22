package jbst.foundation.domain.properties.base;

import jbst.foundation.domain.constants.JbstConstants;
import jbst.foundation.domain.properties.JbstProperty;
import jbst.foundation.domain.properties.annotations.MandatoryProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.boot.context.properties.bind.ConstructorBinding;

import static jbst.foundation.utilities.random.RandomUtility.randomBoolean;

// Lombok (property-based)
@AllArgsConstructor(onConstructor = @__({@ConstructorBinding}))
@Data
@EqualsAndHashCode(callSuper = true)
public class InvitationsOnInit extends JbstProperty {
    @MandatoryProperty
    private final boolean enabled;

    public static InvitationsOnInit hardcoded() {
        return new InvitationsOnInit(true);
    }

    public static InvitationsOnInit random() {
        return randomBoolean() ? enabled() : disabled();
    }

    public static InvitationsOnInit enabled() {
        return hardcoded();
    }

    public static InvitationsOnInit disabled() {
        return new InvitationsOnInit(false);
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
