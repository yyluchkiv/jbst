package jbst.foundation.domain.properties.configs.security;

import jbst.foundation.domain.properties.JbstProperty;
import jbst.foundation.domain.properties.annotations.MandatoryProperty;
import jbst.foundation.domain.properties.base.JbstPropertyInvitationsOnInit;
import jbst.foundation.domain.properties.base.JbstPropertyUsersOnInit;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.boot.context.properties.bind.ConstructorBinding;

// Lombok (property-based)
@AllArgsConstructor(onConstructor = @__({@ConstructorBinding}))
@Data
@EqualsAndHashCode(callSuper = true)
public class JbstPropertySecurityEssence extends JbstProperty {
    @MandatoryProperty
    private final JbstPropertyUsersOnInit usersOnInit;
    @MandatoryProperty
    private final JbstPropertyInvitationsOnInit invitationsOnInit;

    public static JbstPropertySecurityEssence hardcoded() {
        return new JbstPropertySecurityEssence(
                JbstPropertyUsersOnInit.hardcoded(),
                JbstPropertyInvitationsOnInit.hardcoded()
        );
    }

    public static JbstPropertySecurityEssence random() {
        return new JbstPropertySecurityEssence(
                JbstPropertyUsersOnInit.random(),
                JbstPropertyInvitationsOnInit.random()
        );
    }

    @Override
    public JbstPropertyNodeType getNodeType() {
        return JbstPropertyNodeType.BRANCH;
    }

    @Override
    public boolean isToggle() {
        return false;
    }

    @Override
    public String getNameNonLeaf() {
        return "essence-configs";
    }
}
