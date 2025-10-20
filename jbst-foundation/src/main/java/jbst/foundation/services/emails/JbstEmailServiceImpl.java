package jbst.foundation.services.emails;

import jakarta.activation.DataHandler;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMultipart;
import jakarta.mail.util.ByteArrayDataSource;
import jbst.foundation.domain.properties.JbstProperties;
import jbst.foundation.domain.emails.EmailHTML;
import jbst.foundation.domain.emails.EmailPlainAttachment;
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
public class JbstEmailServiceImpl implements JbstEmailService {

    // Services
    private final JavaMailSender javaMailSender;
    // HTML Engine
    private final SpringTemplateEngine springTemplateEngine;
    // Properties
    private final JbstProperties jbstProperties;

    @Override
    public void sendPlain(String[] to, String subject, String message) {
        var emailConfigs = this.jbstProperties.getEmailConfigs();
        if (emailConfigs.isEnabled()) {
            var mailMessage = new SimpleMailMessage();
            mailMessage.setFrom(emailConfigs.getFrom());
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
    public void sendPlainAttachment(EmailPlainAttachment emailPlainAttachment) {
        var emailConfigs = this.jbstProperties.getEmailConfigs();
        if (emailConfigs.isEnabled()) {
            try {
                var message = this.javaMailSender.createMimeMessage();
                var multipart = new MimeMultipart();

                var part1 = new MimeBodyPart();
                part1.setText(emailPlainAttachment.message());
                multipart.addBodyPart(part1);

                var part2 = new MimeBodyPart();
                var source = new ByteArrayDataSource(emailPlainAttachment.attachmentMessage(), "text/plain; charset=UTF-8");
                part2.setDataHandler(new DataHandler(source));
                part2.setFileName(emailPlainAttachment.attachmentFileName());
                multipart.addBodyPart(part2);

                message.setFrom(emailConfigs.getFrom());
                for (var to : emailPlainAttachment.to()) {
                    message.addRecipients(TO, to);
                }
                message.setSubject(emailPlainAttachment.subject());
                message.setContent(multipart);

                this.javaMailSender.send(message);
            } catch (IOException | MessagingException ex) {
                // ignored
            }
        }
    }

    @Override
    public void sendHTML(EmailHTML emailHTML) {
        var emailConfigs = this.jbstProperties.getEmailConfigs();
        if (emailConfigs.isEnabled()) {
            try {
                var message = this.javaMailSender.createMimeMessage();
                var messageHelper = new MimeMessageHelper(message, MULTIPART_MODE_MIXED_RELATED, UTF_8.name());
                messageHelper.setFrom(emailConfigs.getFrom());
                messageHelper.setTo(emailHTML.to().toArray(new String[0]));
                messageHelper.setSubject(emailHTML.subject());
                var context = new Context();
                context.setVariables(emailHTML.templateVariables());
                messageHelper.setText(
                        this.springTemplateEngine.process(emailHTML.templateName(), context),
                        true
                );
                this.javaMailSender.send(message);
            } catch (MessagingException ex) {
                // ignored
            }
        }
    }
}
