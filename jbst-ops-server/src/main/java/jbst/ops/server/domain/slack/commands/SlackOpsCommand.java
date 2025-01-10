package jbst.ops.server.domain.slack.commands;

import jbst.foundation.utilities.slack.SlackUtility;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Optional;
import java.util.stream.Stream;

import static jbst.foundation.domain.constants.JbstConstants.Symbols.NEWLINE;
import static jbst.foundation.domain.constants.JbstConstants.Symbols.TAB;

// Lombok
@AllArgsConstructor
@Getter
public enum SlackOpsCommand {
    HELP("help", "Shows infrastructure.bot [options ...]"),
    STATUS("status", "Shows servers online statuses: OK / Failure markers"),
    ACTUATORS("actuators", "Shows servers actuator /info (ONLY spring-boot servers)"),
    FS("fs", "Shows servers file systems metadata");

    public static Optional<SlackOpsCommand> findOpt(String command) {
        return Stream.of(SlackOpsCommand.values())
                .filter(op -> op.name().equalsIgnoreCase(command))
                .findFirst();
    }

    public static String getHelpTable() {
        var sb = new StringBuilder("ops:");
        sb.append(NEWLINE);
        var commands = SlackOpsCommand.values();
        for (int i = 0; i < commands.length; i++) {
            var command = commands[i];
            sb.append(TAB);
            sb.append(String.format("%-20s %10s", command.value, command.description));
            if (i < commands.length - 1) {
                sb.append(NEWLINE);
            }
        }
        return SlackUtility.getSlackMessage(sb.toString());
    }

    private final String value;
    private final String description;

    public boolean isActuators() {
        return SlackOpsCommand.ACTUATORS.equals(this);
    }
}
