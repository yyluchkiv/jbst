package jbst.foundation.services.emails;

import jakarta.activation.DataHandler;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMultipart;
import jakarta.mail.util.ByteArrayDataSource;
import jbst.foundation.domain.emails.JbstEmails;
import jbst.foundation.domain.properties.JbstProperties;
import jbst.foundation.services.JbstEmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import static jakarta.mail.Message.RecipientType.TO;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.springframework.mail.javamail.MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED;

@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class JbstEmailServiceEnabled implements JbstEmailService {

    // Services
    private final JavaMailSender javaMailSender;
    // HTML Engine
    private final SpringTemplateEngine springTemplateEngine;
    // Properties
    private final JbstProperties jbstProperties;

    @Override
    public void sendPlain(String[] to, String subject, String message) {
        var emails = this.jbstProperties.getEmails();
        if (emails.isEnabled()) {
            var mailMessage = new SimpleMailMessage();
            mailMessage.setFrom(emails.getFrom());
            mailMessage.setTo(to);
            mailMessage.setSubject(subject);
            mailMessage.setText(message);
            this.javaMailSender.send(mailMessage);
        }
    }

    @Override
    public void sendPlain(List<String> to, String subject, String message) {
        this.sendPlain(to.toArray(new String[0]), subject, message);
    }

    @Override
    public void sendPlain(Set<String> to, String subject, String message) {
        this.sendPlain(to.toArray(new String[0]), subject, message);
    }

    @Override
    public void sendPlainAttachment(JbstEmails.AttachmentAndText data) {
        var emails = this.jbstProperties.getEmails();
        if (emails.isEnabled()) {
            try {
                var message = this.javaMailSender.createMimeMessage();
                var multipart = new MimeMultipart();

                var part1 = new MimeBodyPart();
                part1.setText(data.message());
                multipart.addBodyPart(part1);

                var part2 = new MimeBodyPart();
                var source = new ByteArrayDataSource(data.attachmentMessage(), "text/plain; charset=UTF-8");
                part2.setDataHandler(new DataHandler(source));
                part2.setFileName(data.attachmentFileName());
                multipart.addBodyPart(part2);

                message.setFrom(emails.getFrom());
                for (var to : data.to()) {
                    message.addRecipients(TO, to);
                }
                message.setSubject(data.subject());
                message.setContent(multipart);

                this.javaMailSender.send(message);
            } catch (IOException | MessagingException ex) {
                // ignored
            }
        }
    }

    @Override
    public void sendHTML(JbstEmails.HTML data) {
        var emails = this.jbstProperties.getEmails();
        if (emails.isEnabled()) {
            try {
                var message = this.javaMailSender.createMimeMessage();
                var messageHelper = new MimeMessageHelper(message, MULTIPART_MODE_MIXED_RELATED, UTF_8.name());
                messageHelper.setFrom(emails.getFrom());
                messageHelper.setTo(data.to().toArray(new String[0]));
                messageHelper.setSubject(data.subject());
                var context = new Context();
                context.setVariables(data.templateVariables());
                messageHelper.setText(
                        this.springTemplateEngine.process(data.templateName(), context),
                        true
                );
                this.javaMailSender.send(message);
            } catch (MessagingException ex) {
                // ignored
            }
        }
    }
}
