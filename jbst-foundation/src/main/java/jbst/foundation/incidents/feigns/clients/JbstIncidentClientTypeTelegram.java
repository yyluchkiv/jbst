package jbst.foundation.incidents.feigns.clients;

import jbst.foundation.domain.properties.JbstProperties;
import jbst.foundation.feigns.telegram.JbstTelegram;
import jbst.foundation.incidents.domain.JbstIncident;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class JbstIncidentClientTypeTelegram implements JbstIncidentClient {

    // Service
    private final JbstTelegram telegram;
    // Properties
    private final JbstProperties jbstProperties;

    @Override
    public void registerIncident(@NotNull JbstIncident incident) {
        // TODO [YYL-incidents] fixme
    }
}
