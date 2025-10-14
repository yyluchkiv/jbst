package jbst.iam.repositories;

import jbst.foundation.domain.base.Username;
import jbst.foundation.domain.databases.JbstSettings;
import jbst.iam.domain.dto.requests.RequestJbstSettings;

public interface JbstSettingsRepository {
    JbstSettings getSettings();
    boolean isPresent();
    long count();
    JbstSettings saveAs(
            Username updatedBy,
            RequestJbstSettings request
    );
}
