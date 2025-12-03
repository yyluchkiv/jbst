package jbst.foundation.domain.http;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jbst.foundation.domain.constants.JbstConstants;
import jbst.foundation.domain.exceptions.JbstExceptions;
import lombok.experimental.UtilityClass;

import java.util.Arrays;

import static java.util.Objects.nonNull;

@UtilityClass
public class JbstHttpCookies {

    public static Cookie createCookie(
            String cookieKey,
            String cookieValue,
            String domain,
            boolean httpOnly,
            int maxAge
    ) {
        var cookie = new Cookie(cookieKey, cookieValue);
        cookie.setPath(JbstConstants.Symbols.SLASH);
        cookie.setDomain(domain);
        cookie.setHttpOnly(httpOnly);
        cookie.setMaxAge(maxAge);
        return cookie;
    }

    public static Cookie createNullCookie(String cookieKey, String domain) {
        return createCookie(
                cookieKey,
                null,
                domain,
                true,
                0
        );
    }

    public static String readCookie(HttpServletRequest request, String cookieKey) throws JbstExceptions.CookieNotFound {
        var cookies = request.getCookies();
        if (nonNull(cookies)) {
            var cookieOpt = Arrays.stream(cookies)
                    .filter(cookie -> cookie.getName().equals(cookieKey))
                    .findFirst();
            if (cookieOpt.isPresent()) {
                return cookieOpt.get().getValue();
            }
        }
        throw new JbstExceptions.CookieNotFound(cookieKey);
    }
}
