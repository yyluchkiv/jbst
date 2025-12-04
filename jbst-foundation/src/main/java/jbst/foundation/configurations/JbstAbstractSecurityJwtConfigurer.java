package jbst.foundation.configurations;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;

public interface JbstAbstractSecurityJwtConfigurer {
    void configure(WebSecurity web);
    void configure(HttpSecurity http) throws Exception;
}
