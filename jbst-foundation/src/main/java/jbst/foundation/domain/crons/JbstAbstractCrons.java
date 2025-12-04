package jbst.foundation.domain.crons;

import jbst.foundation.domain.properties.base.JbstPropertyCron;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public abstract class JbstAbstractCrons {

    public abstract void processException(Exception ex);

    @SuppressWarnings("unused")
    public void executeCron(JbstPropertyCron cron, Action action) {
        this.executeCron(
                cron.isEnabled(),
                action
        );
    }

    public void alwaysExecuteCron(Action action) {
        this.executeCron(
                true,
                action
        );
    }

    // =================================================================================================================
    // CLASSES
    // =================================================================================================================
    @FunctionalInterface
    public interface Action {
        void execute() throws Exception;
    }


    // ================================================================================================================
    // PRIVATE METHODS
    // ================================================================================================================
    public void executeCron(boolean enabled, Action action) {
        try {
            if (enabled) {
                action.execute();
            }
        } catch (Exception ex) {
            this.processException(ex);
        }
    }
}
