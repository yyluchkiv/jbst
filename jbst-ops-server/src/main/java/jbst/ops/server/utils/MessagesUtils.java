package jbst.ops.server.utils;

import jbst.foundation.domain.tuples.Tuple2;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.queue.CircularFifoQueue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import jbst.ops.server.domain.incidents.OpsIncident;
import jbst.ops.server.domain.keywords.Operation;
import jbst.ops.server.domain.slack.requests.SlackRequestContext;
import jbst.ops.server.properties.OpsProperties;
import jbst.ops.server.properties.configs.MessagesConfigs;

import java.util.List;
import java.util.stream.Collectors;

import static jbst.foundation.domain.constants.JbstConstants.Symbols.NEWLINE;
import static jbst.foundation.domain.constants.JbstConstants.Symbols.TWO_NEWLINE;
import static jbst.foundation.utilities.exceptions.TraceUtility.getTrace;
import static jbst.ops.server.utils.SlackUtils.getSlackMessage;

@Slf4j
@Component
public class MessagesUtils {

    // Messages
    private final MessagesConfigs messagesConfigs;
    private final List<String> founders;

    @Autowired
    public MessagesUtils(OpsProperties opsProperties) {
        this.messagesConfigs = opsProperties.getMessagesConfigs();
        this.founders = opsProperties.getTech1SlackConfigs().getFounders();
    }

    public String getHelp() {
        return this.messagesConfigs.getHelp();
    }

    public String getTaggedFounders() {
        return this.founders.stream()
                .map(assignee -> "<@" + assignee + ">")
                .collect(Collectors.joining(", "));
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
        return this.messagesConfigs.getExpensiveOperationStarted();
    }

    public String getExpensiveOperationCompletedMessage() {
        return this.messagesConfigs.getExpensiveOperationCompleted();
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

    public String getExpiredAccessCodeMessage(String accessCode) {
        return String.format(
                this.messagesConfigs.getExpiredAccessCode(),
                accessCode
        );
    }

    public String getReadOnlyWarning() {
        return this.messagesConfigs.getReadOnlyWarning();
    }

    public String getUnexpectedWarning() {
        return this.messagesConfigs.getUnexpectedWarning();
    }

    public String getUnfaithfulMessage(SlackRequestContext requestContext) {
        var taggedFounders = this.getTaggedFounders();
        var urMessage = String.format(
                this.messagesConfigs.getUnfaithfulUserRequest(),
                requestContext.getUsername(),
                requestContext.getUserChannel(),
                requestContext.isDirect(),
                requestContext.getRawContent()
        );
        return taggedFounders + TWO_NEWLINE + urMessage;
    }

    public String getUnfaithfulMessage(SlackRequestContext requestContext, Throwable throwable) {
        var baseUnfaithfulMessage = getUnfaithfulMessage(requestContext);
        var throwableText = getTrace(throwable).value();
        return baseUnfaithfulMessage + NEWLINE + getSlackMessage(throwableText);
    }

    public Tuple2<String, String> getIncidentTuple(OpsIncident opsIncident) {
        var incident = this.messagesConfigs.getIncident();
        return new Tuple2<>(
                "<!here>" + TWO_NEWLINE + incident + NEWLINE,
                opsIncident.getPlainMessage()
        );
    }
}
