package jbst.foundation.services;

import jbst.foundation.domain.security.CurrentClientUser;

public interface JbstSynchronousExtensionService {
    void authenticateAsMagicLink(CurrentClientUser user);
}
