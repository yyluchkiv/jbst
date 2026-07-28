package jbst.foundation.domain.emails;

import jbst.foundation.domain.base.Email;

import java.util.Map;
import java.util.Set;

import static jbst.foundation.domain.random.JbstRandom.randomString;

public class JbstEmails {

    public record HTML(Set<String> to, String subject, String templateName, Map<String, Object> templateVariables) {

        public static HTML of(
                Email to,
                String subject,
                String templateName,
                Map<String, Object> templateVariables
        ) {
            return new HTML(Set.of(to.value()), subject, templateName, templateVariables);
        }

        public static HTML fixed() {
            return HTML.of(Email.fixed(), "Account Accessed", "jbst-account-accessed", Map.of());
        }

        public static HTML random() {
            return HTML.of(Email.random(), randomString(), randomString(), Map.of());
        }
    }

    public record AttachmentAndText(
            Set<String> to,
            String subject,
            String message,
            String attachmentFileName,
            String attachmentMessage
    ) {
    }
}
