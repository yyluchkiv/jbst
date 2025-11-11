package jbst.foundation.domain.properties;

import jbst.foundation.domain.properties.configs.*;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.Ordered;
import org.springframework.core.PriorityOrdered;

@Slf4j
@ConfigurationProperties(
        prefix = "jbst",
        ignoreUnknownFields = false
)
@Data
public class JbstProperties implements PriorityOrdered {
    private JbstPropertyApp app;
    private JbstPropertyUtils utils;
    private JbstPropertyAsync async;
    private JbstPropertyEvents events;
    private JbstPropertyMVC mvc;
    private JbstPropertyEmails emails;
    private JbstPropertyIncidentsManager incidentsManager;
    private JbstPropertySecurity security;
    private JbstPropertyDatabases databases;

    public static JbstProperties hardcoded() {
        var properties = new JbstProperties();
        properties.setApp(JbstPropertyApp.hardcoded());
        properties.setUtils(JbstPropertyUtils.hardcoded());
        properties.setAsync(JbstPropertyAsync.hardcoded());
        properties.setEvents(JbstPropertyEvents.hardcoded());
        properties.setMvc(JbstPropertyMVC.hardcoded());
        properties.setEmails(JbstPropertyEmails.hardcoded());
        properties.setIncidentsManager(JbstPropertyIncidentsManager.hardcoded());
        properties.setSecurity(JbstPropertySecurity.hardcoded());
        properties.setDatabases(JbstPropertyDatabases.hardcoded());
        return properties;
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }

    // ================================================================================================================
    // MERGED METHODS
    // ================================================================================================================
    public final String getMagicLink(@NotNull String token) {
        return "%s%s?token=%s".formatted(
                this.app.getWebclientURL(),
                this.security.getUsersTokens().getWebclientMagicLinkPath(),
                token
        );
    }

    public final String getEmailConfirmationRedirectLink() {
        return "%s%s".formatted(
                this.app.getWebclientURL(),
                this.security.getUsersTokens().getWebclientEmailConfirmationRedirectPath()
        );
    }

    public final String getEmailConfirmationLink(@NotNull ServerProperties serverProperties, @NotNull String token) {
        return "%s%s/tokens/email/confirm?token=%s".formatted(
                this.app.getServerURL() + serverProperties.getServlet().getContextPath(),
                this.mvc.getBasePathPrefix(),
                token
        );
    }

    public final String getPasswordResetLink(@NotNull String token) {
        return "%s%s?token=%s".formatted(
                this.app.getWebclientURL(),
                this.security.getUsersTokens().getWebclientPasswordResetPath(),
                token
        );
    }
}
