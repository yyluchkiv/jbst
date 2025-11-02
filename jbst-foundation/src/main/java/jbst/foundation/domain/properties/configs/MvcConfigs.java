package jbst.foundation.domain.properties.configs;

import jbst.foundation.domain.properties.JbstProperty;
import jbst.foundation.domain.properties.annotations.MandatoryProperty;
import jbst.foundation.domain.properties.annotations.MandatoryPropertyToggle;
import jbst.foundation.domain.properties.configs.mvc.JbstPropertyCORS;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.boot.context.properties.bind.ConstructorBinding;

import static jbst.foundation.utilities.random.RandomUtility.randomBoolean;
import static jbst.foundation.utilities.random.RandomUtility.randomString;

// Lombok (property-based)
@AllArgsConstructor(onConstructor = @__({@ConstructorBinding}))
@Data
@EqualsAndHashCode(callSuper = true)
public class MvcConfigs extends JbstProperty {
    @MandatoryProperty
    private final boolean enabled;
    @MandatoryPropertyToggle
    private String basePathPrefix;
    @MandatoryPropertyToggle
    private JbstPropertyCORS corsConfigs;

    public static MvcConfigs hardcoded() {
        return new MvcConfigs(
                true,
                "/jbst/security",
                JbstPropertyCORS.hardcoded()
        );
    }

    public static MvcConfigs random() {
        return new MvcConfigs(
                randomBoolean(),
                randomString(),
                JbstPropertyCORS.random()
        );
    }

    public static MvcConfigs enabled() {
        return hardcoded();
    }

    public static MvcConfigs disabled() {
        return new MvcConfigs(
                false,
                randomString(),
                JbstPropertyCORS.random()
        );
    }

    @Override
    public JbstPropertyNodeType getNodeType() {
        return JbstPropertyNodeType.ROOT;
    }

    @Override
    public boolean isToggle() {
        return this.enabled;
    }

    @Override
    public String getNameNonLeaf() {
        return "mvc-configs";
    }
}
