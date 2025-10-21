package jbst.foundation.extension;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jbst.foundation.domain.base.Username;

public interface JbstExtensionService {
    void authenticateAsStandard(Username username, HttpServletRequest request, HttpServletResponse response);
    void authenticateAsMagicLink(Username username, HttpServletRequest request, HttpServletResponse response);
}
