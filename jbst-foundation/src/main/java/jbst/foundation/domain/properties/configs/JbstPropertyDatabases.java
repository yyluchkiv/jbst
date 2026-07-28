package jbst.foundation.domain.properties.configs;

import jbst.foundation.domain.properties.JbstProperty;
import jbst.foundation.domain.properties.annotations.JbstPropertyOptional;
import jbst.foundation.domain.properties.configs.databases.JbstPropertyDatabaseMongo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.boot.context.properties.bind.ConstructorBinding;

// Lombok (property-based)
@AllArgsConstructor(onConstructor = @__({@ConstructorBinding}))
@Data
@EqualsAndHashCode(callSuper = true)
public class JbstPropertyDatabases extends JbstProperty {
    @JbstPropertyOptional
    private final JbstPropertyDatabaseMongo mongo;

    public static JbstPropertyDatabases fixed() {
        return new JbstPropertyDatabases(
                JbstPropertyDatabaseMongo.fixed()
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
        return "databases";
    }
}
