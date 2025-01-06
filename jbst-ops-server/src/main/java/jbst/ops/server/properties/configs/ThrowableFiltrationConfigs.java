package jbst.ops.server.properties.configs;

import jbst.foundation.domain.properties.annotations.MandatoryProperty;
import jbst.foundation.domain.properties.base.AbstractTogglePropertyConfigs;
import jbst.foundation.incidents.domain.Incident;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.boot.context.properties.bind.ConstructorBinding;

import java.util.List;

import static jbst.foundation.incidents.domain.IncidentAttributes.IncidentsTypes.THROWABLE;
import static jbst.foundation.incidents.domain.IncidentAttributes.Keys.TRACE;

// Lombok (property-based)
@AllArgsConstructor(onConstructor = @__({@ConstructorBinding}))
@Data
@EqualsAndHashCode(callSuper = true)
public class ThrowableFiltrationConfigs extends AbstractTogglePropertyConfigs {
    @MandatoryProperty
    private final boolean enabled;
    @MandatoryProperty
    private final List<ThrowableFiltrationTraceConfigs> values;

    public boolean filterOnConfigsAndReturnSkip(Incident incident) {
        if (!this.enabled || !THROWABLE.equals(incident.getType())) {
            return false;
        }
        var trace = incident.getAttributes().get(TRACE).toString();
        var traceConfigsOpt = this.values.stream()
                .filter(item -> trace.contains(item.getTrace()))
                .findFirst();
        if (traceConfigsOpt.isPresent()) {
            var traceConfigs = traceConfigsOpt.get();
            incident.setType(traceConfigs.getIncidentType());
            return !traceConfigs.isEnabled();
        } else {
            return false;
        }
    }
}
