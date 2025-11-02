package jbst.foundation.domain.properties;

import jbst.foundation.domain.properties.configs.*;
import jbst.foundation.domain.properties.configs.security.JbstPropertySecurityMongo;
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
    private ServerConfigs serverConfigs;
    private UtilsConfigs utilsConfigs;
    private AsyncConfigs asyncConfigs;
    private EventsConfigs eventsConfigs;
    private MvcConfigs mvcConfigs;
    private EmailConfigs emailConfigs;
    private JbstPropertyIncidentsManager incidentsManager;
    private SecurityJwtConfigs securityJwtConfigs;
    private JbstPropertySecurityMongo mongodbSecurityJwtConfigs;

    public static JbstProperties hardcoded() {
        var properties = new JbstProperties();
        properties.setServerConfigs(ServerConfigs.hardcoded());
        properties.setUtilsConfigs(UtilsConfigs.hardcoded());
        properties.setAsyncConfigs(AsyncConfigs.hardcoded());
        properties.setEventsConfigs(EventsConfigs.hardcoded());
        properties.setMvcConfigs(MvcConfigs.hardcoded());
        properties.setEmailConfigs(EmailConfigs.hardcoded());
        properties.setIncidentsManager(JbstPropertyIncidentsManager.hardcoded());
        properties.setSecurityJwtConfigs(SecurityJwtConfigs.hardcoded());
        properties.setMongodbSecurityJwtConfigs(JbstPropertySecurityMongo.hardcoded());
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
                this.serverConfigs.getWebclientURL(),
                this.securityJwtConfigs.getUsersTokensConfigs().getWebclientMagicLinkPath(),
                token
        );
    }

    public final String getEmailConfirmationRedirectLink() {
        return "%s%s".formatted(
                this.serverConfigs.getWebclientURL(),
                this.securityJwtConfigs.getUsersTokensConfigs().getWebclientEmailConfirmationRedirectPath()
        );
    }

    public final String getEmailConfirmationLink(@NotNull ServerProperties serverProperties, @NotNull String token) {
        return "%s%s/tokens/email/confirm?token=%s".formatted(
                this.serverConfigs.getServerURL() + serverProperties.getServlet().getContextPath(),
                this.mvcConfigs.getBasePathPrefix(),
                token
        );
    }

    public final String getPasswordResetLink(@NotNull String token) {
        return "%s%s?token=%s".formatted(
                this.serverConfigs.getWebclientURL(),
                this.securityJwtConfigs.getUsersTokensConfigs().getWebclientPasswordResetPath(),
                token
        );
    }
}
