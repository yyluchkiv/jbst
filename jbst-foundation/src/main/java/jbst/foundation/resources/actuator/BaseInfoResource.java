package jbst.foundation.resources.actuator;

import jbst.foundation.domain.properties.JbstProperties;
import jbst.foundation.utilities.environment.EnvironmentUtility;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.info.Info;
import org.springframework.boot.actuate.info.InfoContributor;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class BaseInfoResource implements InfoContributor {

    // Utilities
    private final EnvironmentUtility environmentUtility;
    // Properties
    private final JbstProperties jbstProperties;

    @Override
    public void contribute(Info.Builder builder) {
        Map<String, Object> details = new HashMap<>();
        details.put("activeProfile", this.environmentUtility.getOneActiveProfileOrDash());
        details.put("maven", this.jbstProperties.getServerConfigs().getMavenConfigs().asMavenDetails());
        builder.withDetails(details);
    }
}
