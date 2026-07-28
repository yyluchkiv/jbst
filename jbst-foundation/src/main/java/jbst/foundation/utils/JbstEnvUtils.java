package jbst.foundation.utils;

import jbst.foundation.domain.constants.JbstConstants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static jbst.foundation.domain.asserts.JbstAsserts.assertTrueOrThrow;
import static org.springframework.util.CollectionUtils.isEmpty;

@Slf4j
@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public final class JbstEnvUtils {

    private final Environment environment;

    public void verifyOneActiveProfile() {
        assertTrueOrThrow(this.environment.getActiveProfiles().length == 1, "Expected one active profile");
    }

    public String getOneActiveProfile() {
        return this.environment.getActiveProfiles()[0];
    }

    public String getOneActiveProfileOrDash() {
        var activeProfiles = new ArrayList<>(List.of(this.environment.getActiveProfiles()));
        if (isEmpty(activeProfiles) || activeProfiles.size() > 1) {
            return JbstConstants.Symbols.DASH;
        } else {
            return activeProfiles.getFirst();
        }
    }

    public boolean isDev() {
        return this.isProfile("dev");
    }

    public boolean isStage() {
        return this.isProfile("stage");
    }

    public boolean isProd() {
        return this.isProfile("prod");
    }

    public boolean isProfile(String profile) {
        return profile.equals(this.getOneActiveProfile());
    }
}
