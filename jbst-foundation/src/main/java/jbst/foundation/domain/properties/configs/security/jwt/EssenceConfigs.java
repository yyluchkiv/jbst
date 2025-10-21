package jbst.foundation.domain.properties.configs.security.jwt;

import jbst.foundation.domain.properties.AbstractJbstProperty;
import jbst.foundation.domain.properties.annotations.MandatoryProperty;
import jbst.foundation.domain.properties.base.InvitationsOnInit;
import jbst.foundation.domain.properties.base.UsersOnInit;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.boot.context.properties.bind.ConstructorBinding;

// Lombok (property-based)
@AllArgsConstructor(onConstructor = @__({@ConstructorBinding}))
@Data
@EqualsAndHashCode(callSuper = true)
public class EssenceConfigs extends AbstractJbstProperty {
    @MandatoryProperty
    private final UsersOnInit usersOnInit;
    @MandatoryProperty
    private final InvitationsOnInit invitationsOnInit;

    public static EssenceConfigs hardcoded() {
        return new EssenceConfigs(
                UsersOnInit.hardcoded(),
                InvitationsOnInit.hardcoded()
        );
    }

    public static EssenceConfigs random() {
        return new EssenceConfigs(
                UsersOnInit.random(),
                InvitationsOnInit.random()
        );
    }

    @Override
    public boolean isParent() {
        return false;
    }

    @Override
    public boolean isLeaf() {
        return false;
    }

    @Override
    public boolean isToggle() {
        return false;
    }

    @Override
    public String getNameNonMandatory() {
        return "essence-configs";
    }
}
