package jbst.foundation.resources.base;

import io.swagger.v3.oas.annotations.tags.Tag;
import jbst.foundation.domain.annotations.JbstResource;
import jbst.foundation.domain.dto.responses.JbstResponseUserSessionsTable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// Swagger
@Tag(name = "[jbst] Fixed API")
// Spring
@Slf4j
@JbstResource
@RestController
@RequestMapping("/fixed")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class JbstFixedResource {

    @GetMapping("/sessions")
    public JbstResponseUserSessionsTable getSessions() {
        return JbstResponseUserSessionsTable.random();
    }
}

