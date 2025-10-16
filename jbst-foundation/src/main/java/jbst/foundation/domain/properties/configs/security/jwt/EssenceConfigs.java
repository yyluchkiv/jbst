package jbst.foundation.domain.properties.configs.security.jwt;

import jbst.foundation.domain.properties.annotations.MandatoryProperty;
import jbst.foundation.domain.properties.base.UsersOnInit;
import jbst.foundation.domain.properties.base.Invitations;
import jbst.foundation.domain.properties.configs.AbstractPropertiesConfigs;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.boot.context.properties.bind.ConstructorBinding;

// Lombok (property-based)
@AllArgsConstructor(onConstructor = @__({@ConstructorBinding}))
@Data
@EqualsAndHashCode(callSuper = true)
public class EssenceConfigs extends AbstractPropertiesConfigs {
    @MandatoryProperty
    private final UsersOnInit usersOnInit;
    @MandatoryProperty
    private final Invitations invitations;

    public static EssenceConfigs hardcoded() {
        return new EssenceConfigs(
                UsersOnInit.hardcoded(),
                Invitations.hardcoded()
        );
    }

    public static EssenceConfigs random() {
        return new EssenceConfigs(
                UsersOnInit.random(),
                Invitations.random()
        );
    }

    @Override
    public boolean isParentPropertiesNode() {
        return false;
    }
}
