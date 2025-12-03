package jbst.foundation.domain.properties.configs.databases;

import jbst.foundation.domain.properties.JbstProperty;
import jbst.foundation.domain.properties.annotations.JbstPropertyMandatory;
import jbst.foundation.domain.properties.annotations.JbstPropertyMandatoryOnToggleEnabled;
import jbst.foundation.domain.properties.base.JbstPropertyMongo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.boot.context.properties.bind.ConstructorBinding;

import static jbst.foundation.domain.random.JbstRandom.randomBoolean;

// Lombok (property-based)
@AllArgsConstructor(onConstructor = @__({@ConstructorBinding}))
@Data
@EqualsAndHashCode(callSuper = true)
public class JbstPropertyDatabaseMongo extends JbstProperty {
    @JbstPropertyMandatory
    private final boolean enabled;
    @JbstPropertyMandatoryOnToggleEnabled
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
