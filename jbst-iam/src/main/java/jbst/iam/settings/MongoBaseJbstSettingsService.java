package jbst.iam.settings;

import jbst.foundation.domain.base.Username;
import jbst.foundation.domain.properties.JbstSettingsOnInit;
import jbst.iam.repositories.mongodb.MongoJbstSettingsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class MongoBaseJbstSettingsService implements JbstSettingsService {
    // Repositories
    protected final MongoJbstSettingsRepository settingsRepository;

    @Override
    public void saveOnInit(JbstSettingsOnInit jbstSettingsOnInit) {
        this.settingsRepository.saveAs(
                Username.ops(),
                jbstSettingsOnInit.getHardwareMonitoringThresholds()
        );
    }
}
