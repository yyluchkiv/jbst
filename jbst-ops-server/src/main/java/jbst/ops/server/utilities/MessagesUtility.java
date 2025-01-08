package jbst.ops.server.utilities;

import lombok.experimental.UtilityClass;

@UtilityClass
public class MessagesUtility {

    public static String getReadOnlyWarning() {
        return ":x: Slack bot is READ-ONLY mode. Please contact primary workspace owner";
    }

    // TODO [YYL] add better message
    public static String getUnexpectedWarning() {
        return ":x: Slack bot unexpected behaviour. Please contact primary workspace owner";
    }

    public static String getExpensiveOperationStartedMessage() {
        return ":arrows_counterclockwise: Operation is expensive. Please wait a moment...";
    }

    public static String getExpensiveOperationCompletedMessage() {
        return ":pray: Operation is completed. Thanks for you patience!";
    }
}
