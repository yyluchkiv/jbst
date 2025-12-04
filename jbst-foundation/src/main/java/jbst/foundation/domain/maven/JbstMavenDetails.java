package jbst.foundation.domain.maven;

import jbst.foundation.domain.base.Version;
import jbst.foundation.domain.constants.JbstConstants;

public record JbstMavenDetails(String groupId, String artifactId, Version version) {

    public static JbstMavenDetails hardcoded() {
        return new JbstMavenDetails("jbst", "jbst", Version.hardcoded());
    }

    public static JbstMavenDetails undefined() {
        return new JbstMavenDetails(JbstConstants.Strings.UNDEFINED, JbstConstants.Strings.UNDEFINED, Version.undefined());
    }

    public static JbstMavenDetails dash() {
        return new JbstMavenDetails(JbstConstants.Symbols.DASH, JbstConstants.Symbols.DASH, Version.dash());
    }
}
