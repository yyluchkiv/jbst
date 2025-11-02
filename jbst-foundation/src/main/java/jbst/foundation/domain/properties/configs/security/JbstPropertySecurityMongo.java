package jbst.foundation.domain.properties.configs.security;

import jbst.foundation.domain.properties.JbstProperty;
import jbst.foundation.domain.properties.annotations.MandatoryProperty;
import jbst.foundation.domain.properties.base.Mongodb;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.boot.context.properties.bind.ConstructorBinding;

// Lombok (property-based)
@AllArgsConstructor(onConstructor = @__({@ConstructorBinding}))
@Data
@EqualsAndHashCode(callSuper = true)
public class JbstPropertySecurityMongo extends JbstProperty {
    @MandatoryProperty
    private final Mongodb mongodb;

    public static JbstPropertySecurityMongo hardcoded() {
        return new JbstPropertySecurityMongo(
                Mongodb.hardcoded()
        );
    }

    public static JbstPropertySecurityMongo random() {
        return new JbstPropertySecurityMongo(
                Mongodb.random()
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
        return "mongodb-security-jwt-configs";
    }
}
