package jbst.foundation.configurations;

import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import java.lang.reflect.Method;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith({ SpringExtension.class })
@ContextConfiguration(loader= AnnotationConfigContextLoader.class)
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
class JbstConfigurationJasyptTest {

    @Configuration
    @Import({
            TestJbstConfigurationPropertiesHardcoded.class,
            JbstConfigurationJasypt.class
    })
    static class ContextConfiguration {

    }

    private final JbstConfigurationJasypt componentUnderTest;

    @Test
    void beansTests() {
        // Act
        var methods = Stream.of(this.componentUnderTest.getClass().getMethods())
                .map(Method::getName)
                .collect(Collectors.toList());

        // Assert
        assertThat(methods).hasSize(13);
    }

    @Test
    void annotationTest() {
        // Assert
        assertThat(this.componentUnderTest).isNotNull();
        assertThat(JbstConfigurationJasypt.class.isAnnotationPresent(EnableEncryptableProperties.class)).isTrue();
    }
}
