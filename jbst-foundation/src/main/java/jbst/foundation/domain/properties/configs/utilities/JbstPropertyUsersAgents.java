package jbst.foundation.domain.properties.configs.utilities;

import jbst.foundation.domain.constants.JbstConstants;
import jbst.foundation.domain.properties.JbstProperty;
import jbst.foundation.domain.properties.annotations.JbstPropertyMandatory;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.boot.context.properties.bind.ConstructorBinding;

import static jbst.foundation.domain.random.JbstRandom.randomBoolean;

@AllArgsConstructor(onConstructor = @__({@ConstructorBinding}))
@Data
@EqualsAndHashCode(callSuper = true)
public class JbstPropertyUsersAgents extends JbstProperty {
    @JbstPropertyMandatory
    private final boolean enabled;

    public static JbstPropertyUsersAgents fixed() {
        return new JbstPropertyUsersAgents(true);
    }

    public static JbstPropertyUsersAgents random() {
        return randomBoolean() ? enabled() : disabled();
    }

    public static JbstPropertyUsersAgents enabled() {
        return fixed();
    }

    public static JbstPropertyUsersAgents disabled() {
        return new JbstPropertyUsersAgents(false);
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
