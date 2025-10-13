package jbst.iam.settings;

import jbst.foundation.domain.base.Username;
import jbst.foundation.domain.properties.JbstSettingsOnInit;
import jbst.iam.domain.db.JbstSettings;
import jbst.iam.repositories.postgres.PostgresJbstSettingsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class PostgresBaseJbstSettingsService implements JbstSettingsService {
    // Repositories
    protected final PostgresJbstSettingsRepository settingsRepository;

    @Override
    public void saveOnInit(JbstSettingsOnInit jbstSettingsOnInit) {
        var jbstSettings = this.settingsRepository.saveAs(
                Username.ops(),
                jbstSettingsOnInit.getHardwareMonitoringThresholds()
        );
        System.out.println("===================================================================================");
        System.out.println(jbstSettings);
        System.out.println("===================================================================================");
    }
}
