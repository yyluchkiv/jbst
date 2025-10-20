package jbst.foundation.services.emails;

import jbst.foundation.domain.emails.EmailHTML;
import jbst.foundation.domain.emails.EmailPlainAttachment;
import jbst.foundation.services.JbstEmailService;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Set;

@Slf4j
public class JbstEmailServiceDisabled implements JbstEmailService {

    @Override
    public void sendPlain(String[] to, String subject, String message) {
        this.logPlain(to, subject, message);
    }

    @Override
    public void sendPlain(List<String> to, String subject, String message) {
        this.logPlain(to.toArray(new String[]{}), subject, message);
    }

    @Override
    public void sendPlain(Set<String> to, String subject, String message) {
        this.logPlain(to.toArray(new String[]{}), subject, message);
    }

    @Override
    public void sendPlainAttachment(EmailPlainAttachment emailPlainAttachment) {
        LOGGER.info("Send email attachment: {}", emailPlainAttachment);
    }

    @Override
    public void sendHTML(EmailHTML emailHTML) {
        LOGGER.info("Send email HTML: {}", emailHTML);
    }

    // ================================================================================================================
    // PRIVATE METHODS
    // ================================================================================================================
    private void logPlain(String[] to, String subject, String message) {
        LOGGER.info("Send email. To: {}. Subject: {}. Message: {}", to, subject, message);
    }

}
