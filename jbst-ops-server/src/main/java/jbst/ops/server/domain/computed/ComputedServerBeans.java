package jbst.ops.server.domain.computed;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Getter
@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ComputedServerBeans {
    // REST Client
    private final RestTemplate restTemplate;
}
