package jbst.foundation.extension;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jbst.foundation.domain.base.Email;
import jbst.foundation.domain.base.Username;

public interface JbstExtensionService {
    void authenticateAsStandard(Username username, HttpServletRequest request, HttpServletResponse response);
    void authenticateAsMagicLink(Username username, HttpServletRequest request, HttpServletResponse response);
    void registerMagicLink(Email email);
    void register0(Username username);
    void register1(Username username);
}
