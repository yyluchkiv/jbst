package jbst.iam.services.base;

import jbst.foundation.domain.properties.JbstProperties;
import jbst.foundation.services.emails.services.EmailService;
import jbst.foundation.domain.databases.JbstUserToken;
import jbst.iam.domain.functions.FunctionAccountAccessed;
import jbst.iam.services.UsersEmailsService;
import jbst.iam.utils.UserEmailUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class BaseUsersEmailsService implements UsersEmailsService {

    // Services
    private final EmailService emailService;
    // Utils
    private final UserEmailUtils userEmailUtils;
    // Properties
    private final JbstProperties jbstProperties;

    @Override
    public void executeMagicLink(JbstUserToken userToken) {
        var emailHTML = this.userEmailUtils.getMagicLinkHTML(userToken);
        this.emailService.sendHTML(emailHTML);
    }

    @Override
    public void executeEmailConfirmation(JbstUserToken userToken) {
        var emailHTML = this.userEmailUtils.getEmailConfirmationHTML(userToken);
        this.emailService.sendHTML(emailHTML);
    }

    @Override
    public void executePasswordReset(JbstUserToken userToken) {
        var emailHTML = this.userEmailUtils.getPasswordResetHTML(userToken);
        this.emailService.sendHTML(emailHTML);
    }

    @Override
    public void executeAuthenticationLogin(FunctionAccountAccessed function) {
        if (!this.jbstProperties.getSecurityJwtConfigs().getUsersEmailsConfigs().getAuthenticationLogin().isEnabled()) {
            return;
        }
        var emailHTML = this.userEmailUtils.getAccountAccessedHTML(function);
        this.emailService.sendHTML(emailHTML);
    }

    @Override
    public void executeSessionRefreshed(FunctionAccountAccessed function) {
        if (!this.jbstProperties.getSecurityJwtConfigs().getUsersEmailsConfigs().getSessionRefreshed().isEnabled()) {
            return;
        }
        var emailHTML = this.userEmailUtils.getAccountAccessedHTML(function);
        this.emailService.sendHTML(emailHTML);
    }
}
