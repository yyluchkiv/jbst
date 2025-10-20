package jbst.foundation.extension;

import jbst.foundation.domain.security.CurrentClientUser;

public interface JbstExtensionService {
    void authenticateAsMagicLink(CurrentClientUser user);
}
