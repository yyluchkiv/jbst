package jbst.foundation.domain.properties.configs;

import jbst.foundation.domain.properties.JbstProperty;
import jbst.foundation.domain.properties.annotations.JbstPropertyMandatory;
import jbst.foundation.domain.properties.annotations.JbstPropertyMandatoryOnToggleEnabled;
import jbst.foundation.domain.properties.configs.mvc.JbstPropertyCORS;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.boot.context.properties.bind.ConstructorBinding;

import static jbst.foundation.domain.random.JbstRandom.randomBoolean;
import static jbst.foundation.domain.random.JbstRandom.randomString;

// Lombok (property-based)
@AllArgsConstructor(onConstructor = @__({@ConstructorBinding}))
@Data
@EqualsAndHashCode(callSuper = true)
public class JbstPropertyMVC extends JbstProperty {
    @JbstPropertyMandatory
    private final boolean enabled;
    @JbstPropertyMandatoryOnToggleEnabled
    private String basePathPrefix;
    @JbstPropertyMandatoryOnToggleEnabled
    private JbstPropertyCORS cors;

    public static JbstPropertyMVC fixed() {
        return new JbstPropertyMVC(
                true,
                "/jbst/security",
                JbstPropertyCORS.fixed()
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
        return fixed();
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
        return "mvc";
    }
}
