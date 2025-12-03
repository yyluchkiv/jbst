package jbst.foundation.domain.emails;

import jbst.foundation.domain.base.Email;

import java.util.Map;
import java.util.Set;

import static jbst.foundation.domain.random.JbstRandom.randomString;

public record EmailHTML(
        Set<String> to,
        String subject,
        String templateName,
        Map<String, Object> templateVariables
) {

    public static EmailHTML of(
            Email to,
            String subject,
            String templateName,
            Map<String, Object> templateVariables
    ) {
        return new EmailHTML(Set.of(to.value()), subject, templateName, templateVariables);
    }

    public static EmailHTML hardcoded() {
        return EmailHTML.of(Email.hardcoded(), "Account Accessed", "jbst-account-accessed", Map.of());
    }

    public static EmailHTML random() {
        return EmailHTML.of(Email.random(), randomString(), randomString(), Map.of());
    }
}
