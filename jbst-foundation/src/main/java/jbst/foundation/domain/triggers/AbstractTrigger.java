package jbst.foundation.domain.triggers;

import jbst.foundation.domain.base.Username;

@SuppressWarnings("unused")
public interface AbstractTrigger {
    Username getUsername();
    TriggerType getTriggerType();
    String getReadableDetails();
}
