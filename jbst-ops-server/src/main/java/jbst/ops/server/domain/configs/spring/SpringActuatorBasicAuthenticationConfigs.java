package jbst.ops.server.domain.configs.spring;

import jbst.foundation.domain.base.Password;
import jbst.foundation.domain.base.Username;

public record SpringActuatorBasicAuthenticationConfigs(
        Username username,
        Password password,
        String healthEndpoint,
        String infoEndpoint
) {
}
