package jbst.foundation.repositories;

import jbst.foundation.domain.base.Username;
import jbst.foundation.domain.databases.JbstSettings;
import jbst.foundation.domain.dto.requests.JbstRequestJbstSettings;

public interface JbstSettingsRepository {
    JbstSettings getSettings();
    boolean isPresent();
    long count();
    JbstSettings saveAs(
            Username updatedBy,
            JbstRequestJbstSettings request
    );
}
