package jbst.foundation.domain.properties.configs;

import jbst.foundation.domain.properties.annotations.MandatoryProperty;
import jbst.foundation.domain.properties.annotations.MandatoryToggleProperty;
import jbst.foundation.domain.properties.base.IncidentsManagerType;
import jbst.foundation.domain.properties.base.RemoteServer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.boot.context.properties.bind.ConstructorBinding;

import static jbst.foundation.utilities.random.RandomUtility.randomBoolean;

// Lombok (property-based)
@AllArgsConstructor(onConstructor = @__({@ConstructorBinding}))
@Data
@EqualsAndHashCode(callSuper = true)
public class IncidentsManagerConfigs extends AbstractTogglePropertiesConfigs {
    @MandatoryProperty
    private final boolean enabled;
    @MandatoryToggleProperty
    private final IncidentsManagerType type;
    @MandatoryToggleProperty
    private RemoteServer remoteServer;

    public static IncidentsManagerConfigs hardcoded() {
        return new IncidentsManagerConfigs(true, IncidentsManagerType.hardcoded(), RemoteServer.hardcoded());
    }

    public static IncidentsManagerConfigs random() {
        return new IncidentsManagerConfigs(randomBoolean(), IncidentsManagerType.random(), RemoteServer.random());
    }

    public static IncidentsManagerConfigs enabled() {
        return hardcoded();
    }

    public static IncidentsManagerConfigs disabled() {
        return new IncidentsManagerConfigs(false, null, null);
    }

    @Override
    public boolean isParentPropertiesNode() {
        return true;
    }

    @Override
    public String getPropertyName() {
        return "incidents-manager-configs";
    }
}
