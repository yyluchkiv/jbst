package jbst.ops.server.slack;

import jbst.ops.server.properties.OpsProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class SlackBotsService {

    // Services
    private final SlackMessagingService slackMessagingService;
    // Properties
    private final OpsProperties opsProperties;

    public final void initialize() {

    }
}
