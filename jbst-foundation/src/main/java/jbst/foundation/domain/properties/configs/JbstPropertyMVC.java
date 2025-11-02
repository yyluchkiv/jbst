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
public class JbstPropertyMVC extends JbstProperty {
    @MandatoryProperty
    private final boolean enabled;
    @MandatoryPropertyToggle
    private String basePathPrefix;
    @MandatoryPropertyToggle
    private JbstPropertyCORS corsConfigs;

    public static JbstPropertyMVC hardcoded() {
        return new JbstPropertyMVC(
                true,
                "/jbst/security",
                JbstPropertyCORS.hardcoded()
        );
    }

    public static JbstPropertyMVC random() {
        return new JbstPropertyMVC(
                randomBoolean(),
                randomString(),
                JbstPropertyCORS.random()
        );
    }

    public static JbstPropertyMVC enabled() {
        return hardcoded();
    }

    public static JbstPropertyMVC disabled() {
        return new JbstPropertyMVC(
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
