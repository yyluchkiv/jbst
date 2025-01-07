package jbst.ops.server.properties.configs;

import jbst.foundation.domain.properties.annotations.MandatoryProperty;
import jbst.foundation.domain.properties.base.AbstractPropertyConfigs;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.boot.context.properties.bind.ConstructorBinding;

// Lombok (property-based)
@AllArgsConstructor(onConstructor = @__({@ConstructorBinding}))
@Data
@EqualsAndHashCode(callSuper = true)
public class MessagesConfigs extends AbstractPropertyConfigs {
    @MandatoryProperty
    private final String help;
    @MandatoryProperty
    private final String botNotConfiguredYet;
    @MandatoryProperty
    private final String botConfigured;
    @MandatoryProperty
    private final String serviceOk;
    @MandatoryProperty
    private final String serviceFailure;
    @MandatoryProperty
    private final String serviceHeader;
    @MandatoryProperty
    private final String responseInfo;
    @MandatoryProperty
    private final String responseWarning;
    @MandatoryProperty
    private final String overExpensiveOperation;
    @MandatoryProperty
    private final String serverHistory;
    @MandatoryProperty
    private final String expiredAccessCode;
    @MandatoryProperty
    private final String unfaithfulUserRequest;
}
