package jbst.foundation.services.emails.services.impl;

import jakarta.activation.DataHandler;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMultipart;
import jakarta.mail.util.ByteArrayDataSource;
import jbst.foundation.domain.properties.JbstProperties;
import jbst.foundation.services.emails.domain.EmailHTML;
import jbst.foundation.services.emails.domain.EmailPlainAttachment;
import jbst.foundation.services.emails.services.EmailService;
import jbst.foundation.services.emails.utils.JbstEmailsUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import static jakarta.mail.Message.RecipientType.TO;

@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class EmailServiceImpl implements EmailService {

    // Services
    private final JavaMailSender javaMailSender;
    // HTML Engine
    private final SpringTemplateEngine springTemplateEngine;
    // Utils
    private final JbstEmailsUtils emailsUtils;
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
                var tuple2 = this.emailsUtils.getMimeMessageTuple2();
                var message = tuple2.a();
                var mmHelper = tuple2.b();
                mmHelper.setFrom(emailConfigs.getFrom());
                mmHelper.setTo(emailHTML.to().toArray(new String[0]));
                mmHelper.setSubject(emailHTML.subject());
                var context = new Context();
                context.setVariables(emailHTML.templateVariables());
                var processedHTML = this.springTemplateEngine.process(emailHTML.templateName(), context);
                mmHelper.setText(processedHTML, true);
                this.javaMailSender.send(message);
            } catch (MessagingException ex) {
                // ignored
            }
        }
    }
}
