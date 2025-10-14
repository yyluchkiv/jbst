package jbst.foundation.configurations;

import jbst.iam.configurations.AbstractTestRunnerResources;
import jbst.iam.configurations.TestConfigurationPropertiesMocked;
import org.springframework.test.context.ContextConfiguration;

@SuppressWarnings("unused")
@ContextConfiguration(classes = {
        TestConfigurationPropertiesMocked.class
})
public abstract class TestRunnerResources2 extends AbstractTestRunnerResources {

}
