package jbst.ops.server.utilities;

import jbst.foundation.domain.tuples.Tuple2;
import jbst.ops.server.domain.incidents.OpsIncident;
import lombok.experimental.UtilityClass;

import static jbst.foundation.domain.constants.JbstConstants.Symbols.NEWLINE;
import static jbst.foundation.domain.constants.JbstConstants.Symbols.TWO_NEWLINE;

@UtilityClass
public class MessagesUtility {

    @Deprecated
    public static String getHelp() {
        return ":keyboard: Options infrastructure.bot :keyboard:";
    }

    @Deprecated
    public static String getBotNotConfiguredYet() {
        return ":rocket: infrastructure.bot is not configured yet. Please wait a moment... :arrows_counterclockwise:";
    }

    @Deprecated
    public static String getBotConfigured() {
        return ":rocket: infrastructure.bot is now configured. Enjoy! :white_check_mark:";
    }

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

    public static String getServiceMessage(boolean anyProblems, String serviceName) {
        return anyProblems ? MessagesUtility.getServiceFailureMessage(serviceName) : MessagesUtility.getServiceOkMessage(serviceName);
    }

    public static String getServiceOkMessage(String serviceName) {
        return String.format(
                ":cloud: Service: *%s* :cloud: \n :white_check_mark: Hi guys, everything is OK! Don't worry, be happy! :+1:",
                serviceName
        );
    }

    public static String getServiceFailureMessage(String serviceName) {
        return String.format(
                ":cloud: Service: *%s* :cloud: \n :no_entry: Oops, we have a problem! Failure Alert :no_entry:",
                serviceName
        );
    }

    @Deprecated
    public static String getServicesOkMessage(String parentServiceName, String subServiceName) {
        return String.format(
                ":cloud: Service: *%s* :cloud: \n :white_check_mark: Hi guys, everything is OK! Don't worry, be happy! :+1:",
                parentServiceName + ", " + subServiceName
        );
    }

    @Deprecated
    public static String getServicesFailureMessage(String parentServiceName, String subServiceName) {
        return String.format(
                ":cloud: Service: *%s* :cloud: \n :no_entry: Oops, we have a problem! Failure Alert :no_entry:",
                parentServiceName + ", " + subServiceName
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
