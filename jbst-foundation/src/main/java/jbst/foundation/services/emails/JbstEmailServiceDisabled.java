package jbst.foundation.services.emails;

import jbst.foundation.domain.emails.JbstEmails;
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
    public void sendPlainAttachment(JbstEmails.AttachmentAndText data) {
        LOGGER.info("Send email attachment: {}", data);
    }

    @Override
    public void sendHTML(JbstEmails.HTML data) {
        LOGGER.info("Send email HTML: {}", data);
    }

    // ================================================================================================================
    // PRIVATE METHODS
    // ================================================================================================================
    private void logPlain(String[] to, String subject, String message) {
        LOGGER.info("Send email. To: {}. Subject: {}. Message: {}", to, subject, message);
    }

}
