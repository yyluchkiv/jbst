package jbst.foundation.resources.system;

import jbst.foundation.domain.properties.JbstProperties;
import jbst.foundation.utils.JbstEnvUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.info.Info;
import org.springframework.boot.actuate.info.InfoContributor;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class JbstActuatorResource implements InfoContributor {

    // Utils
    private final JbstEnvUtils envUtils;
    // Properties
    private final JbstProperties jbstProperties;

    @Override
    public void contribute(Info.Builder builder) {
        Map<String, Object> details = new HashMap<>();
        details.put("activeProfile", this.envUtils.getOneActiveProfileOrDash());
        details.put("maven", this.jbstProperties.getServerConfigs().getMavenConfigs().asMavenDetails());
        builder.withDetails(details);
    }
}
