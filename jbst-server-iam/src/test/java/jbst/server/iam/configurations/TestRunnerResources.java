package jbst.server.iam.configurations;

import tools.jackson.core.JacksonException;
import jbst.foundation.domain.jsons.JbstObjectMappers;
import tools.jackson.databind.ObjectMapper;
import jbst.foundation.handlers.JbstResourceExceptionHandler;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@WebAppConfiguration
@ExtendWith({ SpringExtension.class, MockitoExtension.class })
@ContextConfiguration(classes = {
        TestConfigurationResources.class
})
public abstract class TestRunnerResources {

    protected final ObjectMapper objectMapper = JbstObjectMappers.jackson2Compatible();

    protected MockMvc mvc;

    @Autowired
    protected JbstResourceExceptionHandler resourceExceptionHandler;

    public void beforeByResource(Object object) {
        this.mvc = MockMvcBuilders
                .standaloneSetup(object)
                .setControllerAdvice(this.resourceExceptionHandler)
                .build();
    }

    @SuppressWarnings("unused")
    protected String getContent(Object value) throws JacksonException {
        return this.objectMapper.writeValueAsString(value);
    }
}
