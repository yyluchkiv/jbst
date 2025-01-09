package jbst.ops.server.domain.slack.commands;

import java.util.Optional;
import java.util.stream.Stream;

public enum SlackOpsCommand {
    HELP;

    public static Optional<SlackOpsCommand> findOpt(String command) {
        return Stream.of(SlackOpsCommand.values())
                .filter(op -> op.name().equalsIgnoreCase(command))
                .findFirst();
    }
}
