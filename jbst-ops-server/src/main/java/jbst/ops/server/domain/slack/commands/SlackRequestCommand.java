package jbst.ops.server.domain.slack.commands;

import com.slack.api.model.event.AppMentionEvent;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Slf4j
@Getter
@EqualsAndHashCode
@ToString
public class SlackRequestCommand {
    private final boolean valid;
    @NotNull
    private final String input;
    @Nullable
    private final SlackOpsCommand cmd;

    @SuppressWarnings("RegExpRedundantEscape")
    public SlackRequestCommand(AppMentionEvent event) {
        var eventText = event.getText();
        LOGGER.debug("User command before cleaning: `{}`", eventText);
        // https://stackoverflow.com/questions/19166426/replace-all-text-between-braces-in-java-with-regex/19169486
        eventText = eventText.replaceAll("\\<.*?\\>", "").trim();
        LOGGER.debug("User command after cleaning: `{}`", eventText);
        this.input = eventText;
        var cmds = eventText.split(" ");
        if ("ops".equals(cmds[0]) && cmds.length == 2) {
            var cmdOpt = SlackOpsCommand.findOpt(cmds[1]);
            if (cmdOpt.isPresent()) {
                this.valid = true;
                this.cmd = cmdOpt.get();
            } else {
                this.valid = false;
                this.cmd = null;
            }
        } else {
            this.valid = false;
            this.cmd = null;
        }
    }
}
