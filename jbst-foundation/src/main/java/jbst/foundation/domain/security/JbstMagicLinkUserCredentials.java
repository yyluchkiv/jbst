package jbst.foundation.domain.security;

import jbst.foundation.domain.databases.JbstUserToken;

import java.time.ZoneId;

public record JbstMagicLinkUserCredentials(JbstUserToken userToken, ZoneId zoneId) {
}
