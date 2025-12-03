package jbst.foundation.domain.exceptions;

import jbst.foundation.domain.base.Username;
import lombok.Getter;
import lombok.experimental.UtilityClass;

import static jbst.foundation.domain.strings.JbstMessages.contactDevelopmentTeam;
import static jbst.foundation.domain.strings.JbstMessages.entityNotFound;

@UtilityClass
public class JbstExceptions {
    // =================================================================================================================
    // ANY
    // =================================================================================================================
    public static class CookieNotFound extends Exception {
        public CookieNotFound(String cookieKey) {
            super(entityNotFound("Cookie", cookieKey));
        }
    }

    public static class TooManyRequests extends Exception { }

    public static class UsernameAlreadyExist extends Exception {
        @Getter
        private final Username username;
        public UsernameAlreadyExist(Username username) {
            super();
            this.username = username;
        }
    }

    // =================================================================================================================
    // ANY: IllegalArgumentException
    // =================================================================================================================
    public static class IllegalEnum extends IllegalArgumentException {
        public IllegalEnum(Class<?> enumClazz) {
            super("Please check enum: " + enumClazz);
        }
    }

    public static class UnreachableCode extends IllegalArgumentException {
        public UnreachableCode() {
            super(contactDevelopmentTeam("Unreachable code"));
        }
    }

    // =================================================================================================================
    // AUTHENTICATION
    // =================================================================================================================
    public static class Login extends Exception {
        public Login(String message) {
            super(message);
        }
    }

    public static class PasswordReset extends Exception {
        public PasswordReset(String message) {
            super(message);
        }

        public static PasswordReset userNotFound() {
            return new PasswordReset("User not found");
        }

        public static PasswordReset emailMissing() {
            return new PasswordReset("User email is missing");
        }

        public static PasswordReset emailNotConfirmed() {
            return new PasswordReset("User email is not confirmed");
        }
    }

    public static class Registration extends Exception {
        public Registration(String message) {
            super(message);
        }
    }

    // =================================================================================================================
    // GEO
    // =================================================================================================================
    public static class GeoLocationNotFound extends Exception {
        public GeoLocationNotFound(String message) {
            super("Geo location not found: " + message);
        }
    }

    // =================================================================================================================
    // SSH
    // =================================================================================================================
    public static class SshSession extends Exception {
        public SshSession(Exception ex) {
            super(ex);
        }
    }

    // =================================================================================================================
    // TOKEN(s)
    // =================================================================================================================
    public static class AccessTokenDbNotFound extends Exception {
        public AccessTokenDbNotFound(Username username) {
            super("JWT access token is not present in database. Username: " + username);
        }
    }

    public static class AccessTokenExpired extends Exception {
        public AccessTokenExpired(Username username) {
            super("JWT access token is expired. Username: " + username);
        }
    }

    public static class AccessTokenInvalid extends Exception {
        public AccessTokenInvalid() {
            super("JWT access token is invalid");
        }
    }

    public static class AccessTokenNotFound extends Exception {
        public AccessTokenNotFound() {
            super("JWT access token not found");
        }
    }

    public static class CsrfTokenNotFound extends Exception {
        public CsrfTokenNotFound() {
            super("CSRF token not found");
        }
    }

    public static class ExtensionTokenAccessDenied extends Exception {
        public ExtensionTokenAccessDenied(String message) {
            super(message);
        }
    }

    public static class ExtensionTokenUnauthorized extends Exception {
        public ExtensionTokenUnauthorized(String message) {
            super(message);
        }
    }

    public static class RefreshTokenDbNotFound extends Exception {
        public RefreshTokenDbNotFound(Username username) {
            super("JWT refresh token is not present in database. Username: " + username);
        }
    }

    public static class RefreshTokenExpired extends Exception {
        public RefreshTokenExpired(Username username) {
            super("JWT refresh token is expired. Username: " + username);
        }
    }

    public static class RefreshTokenInvalid extends Exception {
        public RefreshTokenInvalid() {
            super("JWT refresh token is invalid");
        }
    }

    public static class RefreshTokenNotFound extends Exception {
        public RefreshTokenNotFound() {
            super("JWT refresh token not found");
        }
    }

    public static class Unauthorized extends Exception {
        public Unauthorized(String message) {
            super(message);
        }
    }

    // =================================================================================================================
    // User(s)
    // =================================================================================================================
    public static class UserEmailConfirmation extends Exception {
        public UserEmailConfirmation() {
            super("Token not found");
        }
    }

    public class UserTokenValidation extends Exception {
        private UserTokenValidation(String message) {
            super(message);
        }

        public static UserTokenValidation notFound() {
            return new UserTokenValidation("Token not found");
        }

        public static UserTokenValidation used() {
            return new UserTokenValidation("Token is used");
        }

        public static UserTokenValidation expired() {
            return new UserTokenValidation("Token is expired");
        }

        public static UserTokenValidation invalidType() {
            return new UserTokenValidation("Token type is invalid");
        }
    }
}
