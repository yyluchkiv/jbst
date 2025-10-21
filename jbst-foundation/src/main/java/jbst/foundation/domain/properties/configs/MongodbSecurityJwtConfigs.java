package jbst.foundation.domain.properties.configs;

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
public class MongodbSecurityJwtConfigs extends JbstProperty {
    @MandatoryProperty
    private final Mongodb mongodb;

    public static MongodbSecurityJwtConfigs hardcoded() {
        return new MongodbSecurityJwtConfigs(
                Mongodb.hardcoded()
        );
    }

    public static MongodbSecurityJwtConfigs random() {
        return new MongodbSecurityJwtConfigs(
                Mongodb.random()
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
        return "mongodb-security-jwt-configs";
    }
}
