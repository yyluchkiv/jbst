package jbst.foundation.domain.security;

import jbst.foundation.domain.databases.JbstUserToken;

import java.time.ZoneId;

public record MagicLinkUserCredentials(
        JbstUserToken userToken,
        ZoneId zoneId
) {
}
