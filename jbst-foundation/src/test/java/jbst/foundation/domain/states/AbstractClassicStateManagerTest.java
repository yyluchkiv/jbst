package jbst.foundation.domain.states;

import org.junit.jupiter.api.Test;

class AbstractClassicStateManagerTest {

    static class TestClassicStateManager extends AbstractClassicStateManager {

        @Override
        public String getLogKeyword() {
            return "[Test] {}: {} → {}";
        }

        @Override
        public String getLogId() {
            return "JUNIT";
        }
    }

    @Test
    public void print() {
        // Act
        var stateManager = new TestClassicStateManager();
        stateManager.start();
        stateManager.onActivation();
        stateManager.pause();
        stateManager.onPaused();
        stateManager.stop();
        stateManager.onTermination();
        stateManager.complete();
        stateManager.onComplete();
    }
}
