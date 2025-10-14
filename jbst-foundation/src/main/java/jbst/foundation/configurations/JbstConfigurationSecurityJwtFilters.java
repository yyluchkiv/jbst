package jbst.foundation.configurations;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan({
        "jbst.foundation.filters.jwt",
        "jbst.foundation.filters.logging"
})
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class JbstConfigurationSecurityJwtFilters {

}
