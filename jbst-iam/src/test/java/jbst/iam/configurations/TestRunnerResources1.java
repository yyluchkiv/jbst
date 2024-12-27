package jbst.iam.configurations;

import jbst.foundation.configurations.TestJbstConfigurationPropertiesHardcoded;
import org.springframework.test.context.ContextConfiguration;

@ContextConfiguration(classes = {
        TestJbstConfigurationPropertiesHardcoded.class
})
public abstract class TestRunnerResources1 extends AbstractTestRunnerResources {

}
