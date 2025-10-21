package jbst.foundation.domain.properties.configs;

import jbst.foundation.domain.base.PropertyId;
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
public class MongodbSecurityJwtConfigs extends AbstractPropertiesConfigs {
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
    public boolean isParentPropertiesNode() {
        return true;
    }

    @Override
    public PropertyId getPropertyName() {
        return new PropertyId("mongodb-security-jwt-configs");
    }
}
