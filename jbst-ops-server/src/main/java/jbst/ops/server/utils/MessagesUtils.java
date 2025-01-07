package jbst.ops.server.utils;

import jbst.foundation.domain.tuples.Tuple2;
import jbst.ops.server.domain.incidents.OpsIncident;
import jbst.ops.server.domain.keywords.Operation;
import jbst.ops.server.properties.OpsProperties;
import jbst.ops.server.properties.configs.MessagesConfigs;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.queue.CircularFifoQueue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static jbst.foundation.domain.constants.JbstConstants.Symbols.NEWLINE;
import static jbst.foundation.domain.constants.JbstConstants.Symbols.TWO_NEWLINE;

@Slf4j
@Component
public class MessagesUtils {

    // Messages
    private final MessagesConfigs messagesConfigs;

    @Autowired
    public MessagesUtils(OpsProperties opsProperties) {
        this.messagesConfigs = opsProperties.getMessagesConfigs();
    }

    public String getHelp() {
        return this.messagesConfigs.getHelp();
    }

    public String getBotNotConfiguredYet() {
        return this.messagesConfigs.getBotNotConfiguredYet();
    }

    public String getBotConfigured() {
        return this.messagesConfigs.getBotConfigured();
    }

    public String getServiceMessage(boolean anyProblems, String serviceName) {
        return anyProblems ? this.getServiceFailureMessage(serviceName) : this.getServiceOkMessage(serviceName);
    }

    public String getServiceOkMessage(String serviceName) {
        return String.format(
                this.messagesConfigs.getServiceOk(),
                serviceName
        );
    }

    public String getServiceFailureMessage(String serviceName) {
        return String.format(
                this.messagesConfigs.getServiceFailure(),
                serviceName
        );
    }

    public String getServicesMessage(boolean anyProblems, String parentServiceName, String subServiceName) {
        return anyProblems ? this.getServicesFailureMessage(parentServiceName, subServiceName) : this.getServicesOkMessage(parentServiceName, subServiceName);
    }

    public String getServicesOkMessage(String parentServiceName, String subServiceName) {
        return String.format(
                this.messagesConfigs.getServiceOk(),
                parentServiceName + ", " + subServiceName
        );
    }

    public String getServicesFailureMessage(String parentServiceName, String subServiceName) {
        return String.format(
                this.messagesConfigs.getServiceFailure(),
                parentServiceName + ", " + subServiceName
        );
    }

    public String getServiceHeaderMessage(String serviceName) {
        return String.format(
                this.messagesConfigs.getServiceHeader(),
                serviceName
        );
    }

    public String getResponseInfo(Operation operation) {
        return String.format(
                this.messagesConfigs.getResponseInfo(),
                operation.getReadableValue()
        );
    }

    public String getResponseWarnings() {
        return this.messagesConfigs.getResponseWarning();
    }

    public String getExpensiveOperationStartedMessage() {
        return ":arrows_counterclockwise: Operation is expensive. Please wait a moment...";
    }

    public String getExpensiveOperationCompletedMessage() {
        return ":pray: Operation is completed. Thanks for you patience!";
    }

    public String getOverExpensiveOperation() {
        return this.messagesConfigs.getOverExpensiveOperation();
    }

    public String getServerHistoryMessage(String serverName, CircularFifoQueue<Boolean> upHistory) {
        return String.format(
                this.messagesConfigs.getServerHistory(),
                Boolean.TRUE.equals(upHistory.get(upHistory.size() - 1)) ? ":white_check_mark:" : ":no_entry:",
                serverName
        );
    }

    public String getReadOnlyWarning() {
        return ":x: Slack bot is READ-ONLY mode. Please contact primary workspace owner";
    }

    public String getUnexpectedWarning() {
        return ":x: Slack bot unexpected behaviour. Please contact primary workspace owner";
    }

    public Tuple2<String, String> getIncidentTuple(OpsIncident opsIncident) {
        var incident = ":ladybug: Please review incident details and *take actions* to stabilize production environment :ladybug:";
        return new Tuple2<>(
                "<!here>" + TWO_NEWLINE + incident + NEWLINE,
                opsIncident.getPlainMessage()
        );
    }
}
