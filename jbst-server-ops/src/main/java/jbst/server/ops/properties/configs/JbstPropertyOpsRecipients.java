package jbst.server.ops.properties.configs;

import jbst.foundation.domain.constants.JbstConstants;
import jbst.foundation.domain.properties.JbstProperty;
import jbst.foundation.domain.properties.annotations.JbstPropertyMandatory;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.boot.context.properties.bind.ConstructorBinding;

import java.util.List;

@SuppressWarnings("unused")
@AllArgsConstructor(onConstructor = @__({@ConstructorBinding}))
@Data
@EqualsAndHashCode(callSuper = true)
public class JbstPropertyOpsRecipients extends JbstProperty {
    @JbstPropertyMandatory
    private final List<String> to;

    public static JbstPropertyOpsRecipients hardcoded() {
        return new JbstPropertyOpsRecipients(
                List.of(
                        "test1@" + JbstConstants.Domains.HARDCODED,
                        "test2@" + JbstConstants.Domains.HARDCODED
                )
        );
    }

    @Override
    public JbstPropertyNodeType getNodeType() {
        return JbstPropertyNodeType.ROOT;
    }

    @Override
    public boolean isToggle() {
        return false;
    }

    @Override
    public String getNameNonLeaf() {
        return "recipients";
    }
}
