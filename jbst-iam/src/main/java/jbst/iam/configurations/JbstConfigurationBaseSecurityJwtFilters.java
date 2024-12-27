package jbst.iam.configurations;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan({
        "jbst.iam.filters.jwt",
        "jbst.iam.filters.logging"
})
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class JbstConfigurationBaseSecurityJwtFilters {

}
