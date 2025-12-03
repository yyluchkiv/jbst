package jbst.foundation.domain.slack;

import lombok.experimental.UtilityClass;

@UtilityClass
public class JbstSlack {

    public static String getSlackTable(String header, String table) {
        return "```\n%s\n%s\n```".formatted(header, table);
    }

    public static String getSlackMessage(String text) {
        return "```\n%s\n```".formatted(text);
    }
}
