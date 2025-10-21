package jbst.server.ops.properties.configs;

import jbst.foundation.domain.constants.JbstConstants;
import jbst.foundation.domain.properties.JbstProperty;
import jbst.foundation.domain.properties.annotations.MandatoryProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.boot.context.properties.bind.ConstructorBinding;

import java.util.List;

@SuppressWarnings("unused")
@AllArgsConstructor(onConstructor = @__({@ConstructorBinding}))
@Data
@EqualsAndHashCode(callSuper = true)
public class RecipientsConfigs extends JbstProperty {
    @MandatoryProperty
    private final List<String> to;

    public static RecipientsConfigs hardcoded() {
        return new RecipientsConfigs(
                List.of(
                        "test1@" + JbstConstants.Domains.HARDCODED,
                        "test2@" + JbstConstants.Domains.HARDCODED
                )
        );
    }

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
        return "recipients-configs";
    }
}
