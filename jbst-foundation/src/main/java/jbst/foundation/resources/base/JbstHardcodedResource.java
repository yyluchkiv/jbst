package jbst.foundation.resources.base;

import io.swagger.v3.oas.annotations.tags.Tag;
import jbst.foundation.domain.annotations.JbstResource;
import jbst.foundation.domain.dto.responses.ResponseUserSessionsTable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// Swagger
@Tag(name = "[jbst] Hardcoded API")
// Spring
@Slf4j
@JbstResource
@RestController
@RequestMapping("/hardcoded")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class JbstHardcodedResource {

    @GetMapping("/sessions")
    public ResponseUserSessionsTable getSessions() {
        return ResponseUserSessionsTable.random();
    }
}

