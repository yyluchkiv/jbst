package jbst.foundation.utilities.slack;

import lombok.experimental.UtilityClass;

@UtilityClass
public class SlackUtility {

    public static String getSlackTable(String header, String table) {
        return "```\n%s\n%s\n```".formatted(header, table);
    }

    public static String getSlackMessage(String text) {
        return "```\n%s\n```".formatted(text);
    }
}
