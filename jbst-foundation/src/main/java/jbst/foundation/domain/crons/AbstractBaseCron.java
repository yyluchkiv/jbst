package jbst.foundation.domain.crons;

import jbst.foundation.domain.properties.base.JbstPropertyCron;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public abstract class AbstractBaseCron {

    public abstract void processException(Exception ex);

    public void executeCron(JbstPropertyCron cron, AbstractCronAction action) {
        this.executeCron(
                cron.isEnabled(),
                action
        );
    }

    public void alwaysExecuteCron(AbstractCronAction action) {
        this.executeCron(
                true,
                action
        );
    }

    // ================================================================================================================
    // PRIVATE METHODS
    // ================================================================================================================
    public void executeCron(boolean enabled, AbstractCronAction action) {
        try {
            if (enabled) {
                action.execute();
            }
        } catch (Exception ex) {
            this.processException(ex);
        }
    }
}
