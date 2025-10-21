package jbst.server.iam.extension;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jbst.foundation.domain.base.Username;
import jbst.foundation.extension.JbstExtensionService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ServerJbstExtensionService implements JbstExtensionService {

    @Override
    public void authenticateAsStandard(Username username, HttpServletRequest request, HttpServletResponse response) {
        // no required actions
    }

    @Override
    public void authenticateAsMagicLink(Username username, HttpServletRequest request, HttpServletResponse response) {
        // no required actions
    }
}
