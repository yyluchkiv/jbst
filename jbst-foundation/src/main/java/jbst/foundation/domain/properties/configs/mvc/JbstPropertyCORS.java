package jbst.foundation.domain.properties.configs.mvc;

import jbst.foundation.domain.constants.JbstConstants;
import jbst.foundation.domain.properties.JbstProperty;
import jbst.foundation.domain.properties.annotations.JbstPropertyOptional;
import jbst.foundation.domain.random.JbstRandom;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.boot.context.properties.bind.ConstructorBinding;

import java.util.Set;

import static jbst.foundation.domain.random.JbstRandom.randomBoolean;
import static jbst.foundation.domain.random.JbstRandom.randomString;

// Lombok (property-based)
@AllArgsConstructor(onConstructor = @__({@ConstructorBinding}))
@Data
@EqualsAndHashCode(callSuper = true)
public class JbstPropertyCORS extends JbstProperty {
    @JbstPropertyOptional
    private String pathPattern;
    @JbstPropertyOptional
    private String[] allowedOrigins;
    @JbstPropertyOptional
    private String[] allowedMethods;
    @JbstPropertyOptional
    private String[] allowedHeaders;
    @JbstPropertyOptional
    private boolean allowCredentials;
    @JbstPropertyOptional
    private String[] exposedHeaders;

    public static JbstPropertyCORS fixed() {
        return new JbstPropertyCORS(
                "/api/**",
                new String[] { "http://localhost:8080", "http://localhost:8081" },
                new String[] { "GET", "POST" },
                new String[] { "Access-Control-Allow-Origin" },
                true,
                null
        );
    }

    public static JbstPropertyCORS random() {
        return new JbstPropertyCORS(
                randomString(),
                new String[] { randomString(), randomString() },
                new String[] { JbstRandom.randomElement(Set.of("GET", "POST", "PUT", "DELETE")) },
                new String[] { randomString() },
                randomBoolean(),
                new String[] { randomString() }
        );
    }

    @Override
    public JbstPropertyNodeType getNodeType() {
        return JbstPropertyNodeType.LEAF;
    }

    @Override
    public boolean isToggle() {
        return false;
    }

    @Override
    public String getNameNonLeaf() {
        return JbstConstants.Symbols.DASH;
    }
}
