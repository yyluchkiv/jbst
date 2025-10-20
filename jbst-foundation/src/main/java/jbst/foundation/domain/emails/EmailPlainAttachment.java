package jbst.foundation.domain.emails;

import java.util.Set;

public record EmailPlainAttachment(
        Set<String> to,
        String subject,
        String message,
        String attachmentFileName,
        String attachmentMessage
) {
}
