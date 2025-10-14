package jbst.foundation.resources.system;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// Swagger
@Tag(name = "[jbst] System API")
// Spring
@RestController
@RequestMapping("/system")
public class JbstSystemResource {

    @GetMapping("/csrf")
    public String getCsrfToken(HttpServletRequest request) {
        var csrf = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
        return csrf.getToken();
    }
}
