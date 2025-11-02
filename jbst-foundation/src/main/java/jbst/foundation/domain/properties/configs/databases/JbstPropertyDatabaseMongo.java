package jbst.foundation.domain.properties.configs.databases;

import jbst.foundation.domain.properties.JbstProperty;
import jbst.foundation.domain.properties.annotations.MandatoryProperty;
import jbst.foundation.domain.properties.annotations.MandatoryPropertyToggle;
import jbst.foundation.domain.properties.base.JbstPropertyMongo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.boot.context.properties.bind.ConstructorBinding;

import static jbst.foundation.utilities.random.RandomUtility.randomBoolean;

// Lombok (property-based)
@AllArgsConstructor(onConstructor = @__({@ConstructorBinding}))
@Data
@EqualsAndHashCode(callSuper = true)
public class JbstPropertyDatabaseMongo extends JbstProperty {
    @MandatoryProperty
    private final boolean enabled;
    @MandatoryPropertyToggle
    private final JbstPropertyMongo database;

    public static JbstPropertyDatabaseMongo hardcoded() {
        return new JbstPropertyDatabaseMongo(
                true,
                JbstPropertyMongo.hardcoded()
        );
    }

    public static JbstPropertyDatabaseMongo random() {
        return new JbstPropertyDatabaseMongo(
                randomBoolean(),
                JbstPropertyMongo.random()
        );
    }

    @Override
    public JbstPropertyNodeType getNodeType() {
        return JbstPropertyNodeType.BRANCH;
    }

    @Override
    public boolean isToggle() {
        return false;
    }

    @Override
    public String getNameNonLeaf() {
        return "mongo";
    }
}
