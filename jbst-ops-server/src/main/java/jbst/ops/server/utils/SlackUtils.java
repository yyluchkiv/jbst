package jbst.ops.server.utils;

import lombok.experimental.UtilityClass;

import static jbst.foundation.domain.constants.JbstConstants.Symbols.NEWLINE;

@UtilityClass
public class SlackUtils {
    private static final String SLACK_WRAPPER = "```";

    public static String getSlackTable(String header, String table) {
        return SLACK_WRAPPER +
                NEWLINE +
                header +
                NEWLINE +
                table +
                NEWLINE +
                SLACK_WRAPPER;
    }

    public static String getSlackMessage(String text) {
        return SLACK_WRAPPER +
                NEWLINE +
                text +
                NEWLINE +
                SLACK_WRAPPER;
    }
}
