package jbst.server.iam.base.properties;

import jbst.foundation.domain.properties.AbstractJbstProperty;
import jbst.foundation.domain.properties.annotations.MandatoryProperty;
import jbst.server.iam.base.domain.enums.UserAuthority;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.boot.context.properties.bind.ConstructorBinding;

// Lombok (property-based)
@AllArgsConstructor(onConstructor = @__({@ConstructorBinding}))
@Data
@EqualsAndHashCode(callSuper = true)
public class ServerConfigs extends AbstractJbstProperty {
    @MandatoryProperty
    private final String targetAttribute1;
    @MandatoryProperty
    private final long targetAttribute2;
    @MandatoryProperty
    private final UserAuthority targetAuthority;

    @Override
    public boolean isRoot() {
        return true;
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
        return "server-configs";
    }
}
