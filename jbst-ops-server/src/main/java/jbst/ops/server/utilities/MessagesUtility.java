package jbst.ops.server.utilities;

import jbst.foundation.domain.tuples.Tuple2;
import jbst.ops.server.domain.incidents.OpsIncident;
import lombok.experimental.UtilityClass;
import org.apache.commons.collections4.queue.CircularFifoQueue;

import static java.lang.Boolean.TRUE;
import static jbst.foundation.domain.constants.JbstConstants.Symbols.NEWLINE;
import static jbst.foundation.domain.constants.JbstConstants.Symbols.TWO_NEWLINE;

@UtilityClass
public class MessagesUtility {

    public static String getHelpTableHeader() {
        return ":keyboard: Options infrastructure.bot :keyboard:";
    }

    public static String getReadOnlyWarning() {
        return ":x: Slack bot is READ-ONLY mode. Please contact primary workspace owner";
    }

    // TODO [YYL] add better message
    public static String getUnexpectedWarning() {
        return ":x: Slack bot unexpected behaviour. Please contact primary workspace owner";
    }

    // TODO [YYL] WTF?
    public static String getResponseWarnings() {
        return ":warning: Executed operation response. Please resolve provided warning!";
    }

    public static String getOverExpensiveOperation() {
        return ":arrows_counterclockwise: Current operation is over-expensive. Please wait a moment...";
    }

    public static String getExpensiveOperationStartedMessage() {
        return ":arrows_counterclockwise: Operation is expensive. Please wait a moment...";
    }

    public static String getExpensiveOperationCompletedMessage() {
        return ":pray: Operation is completed. Thanks for you patience!";
    }

    public static String getTaskHeader(String taskName) {
        return String.format(
                ":cloud: Service: *%s* :cloud:",
                taskName
        );
    }

    public static String getTaskMessage(String taskName, boolean anyProblems) {
        return anyProblems ? String.format(
                ":cloud: Service: *%s* :cloud: \n :no_entry: Oops, we have a problem! Failure Alert :no_entry:",
                taskName
        ) : String.format(
                ":cloud: Service: *%s* :cloud: \n :white_check_mark: Hi guys, everything is OK! Don't worry, be happy! :+1:",
                taskName
        );
    }

    public static String getServerHistoryMessage(String serverName, CircularFifoQueue<Boolean> upHistory) {
        return String.format(
                "%s Server: *%s*",
                TRUE.equals(upHistory.get(upHistory.size() - 1)) ? ":white_check_mark:" : ":no_entry:",
                serverName
        );
    }

    public static Tuple2<String, String> getIncidentTuple(OpsIncident opsIncident) {
        var incident = ":ladybug: Please review incident details and *take actions* to stabilize production environment :ladybug:";
        return new Tuple2<>(
                "<!here>" + TWO_NEWLINE + incident + NEWLINE,
                opsIncident.getPlainMessage()
        );
    }
}
