package jbst.iam.resources.base;

import io.swagger.v3.oas.annotations.tags.Tag;
import jbst.foundation.domain.annotations.JbstResource;
import jbst.iam.domain.dto.responses.ResponseUserSessionsTable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// Swagger
@Tag(name = "[jbst] Test Data API")
// Spring
@Slf4j
@JbstResource
@RestController
@RequestMapping("/test-data")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class JbstTestDataResource {

    @GetMapping("/sessions")
    public ResponseUserSessionsTable getSessions() {
        return ResponseUserSessionsTable.random();
    }
}

