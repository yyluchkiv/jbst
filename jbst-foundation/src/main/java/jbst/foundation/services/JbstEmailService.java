package jbst.foundation.services;

import jbst.foundation.domain.emails.JbstEmails;
import org.springframework.scheduling.annotation.Async;

import java.util.List;
import java.util.Set;

@Async
public interface JbstEmailService {
    void sendPlain(String[] to, String subject, String message);
    void sendPlain(List<String> to, String subject, String message);
    void sendPlain(Set<String> to, String subject, String message);

    void sendPlainAttachment(JbstEmails.AttachmentAndText data);

    void sendHTML(JbstEmails.HTML data);
}
