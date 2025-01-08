package jbst.ops.server.utils;

import jbst.foundation.domain.tuples.Tuple2;
import jbst.ops.server.domain.incidents.OpsIncident;
import jbst.ops.server.domain.keywords.Operation;
import jbst.ops.server.properties.OpsProperties;
import jbst.ops.server.properties.configs.MessagesConfigs;
import jbst.ops.server.utilities.MessagesUtility;
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
}
