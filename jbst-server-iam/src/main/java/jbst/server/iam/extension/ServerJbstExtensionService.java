package jbst.server.iam.extension;

import jbst.foundation.domain.security.CurrentClientUser;
import jbst.foundation.extension.JbstExtensionService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ServerJbstExtensionService implements JbstExtensionService {

    @Override
    public void authenticateAsMagicLink(CurrentClientUser user) {
        // no required actions
    }
}
