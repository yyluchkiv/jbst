package jbst.ops.server.domain.servers;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jbst.foundation.domain.base.ServerName;
import jbst.foundation.domain.collections.Partitions;
import jbst.foundation.domain.tuples.Tuple2;
import jbst.foundation.feigns.spring.SpringBootClient;
import jbst.ops.server.domain.slack.messages.SlackMessageFileSystemTable;
import jbst.ops.server.domain.slack.messages.SlackMessageServerTable;
import jbst.ops.server.domain.slack.messages.SlackMessageServersSpringActuatorsTable;
import jbst.ops.server.utilities.MessagesUtility;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;
import static jbst.ops.server.constants.OpsConstants.Services.SPRING_BOOT_ACTUATORS_SERVICE;
import static jbst.ops.server.constants.OpsConstants.Services.STATUS_SERVICE;
import static jbst.ops.server.domain.servers.ServerFileSystemMetadata.FileSystemMetadataRow.PERCENTAGE_REVERSED;
import static org.springframework.util.CollectionUtils.isEmpty;

// Lombok
@Getter
@EqualsAndHashCode
@ToString
public class Servers {
    private final List<Server> values;
    private final Map<Team, List<Server>> mappedValues;
    private final List<Tuple2<ServerName, SpringBootClient.SpringBootActuatorInfo>> mappedActuatorsResponses;
    private final boolean anyPresent;
    private final boolean anyChanges;
    private final boolean anyProblems;
    private final boolean anyProblemsOnSpringBootActuators;

    public Servers(@NotNull List<Server> values) {
        this.values = values;
        this.mappedValues = values.stream().collect(Collectors.groupingBy(Server::team));
        this.mappedActuatorsResponses = values.stream()
                .map(server -> new Tuple2<>(server.name(), server.springBootActuatorInfo()))
                .collect(Collectors.toList());
        this.anyPresent = !isEmpty(values);
        this.anyChanges = values.stream().anyMatch(Server::anyChanges);
        this.anyProblems = values.stream().anyMatch(server -> !server.ok());
        this.anyProblemsOnSpringBootActuators = values.stream()
                .filter(server -> server.type().isServerSpringBoot())
                .anyMatch(server -> isNull(server.springBootActuatorInfo())
                        || isNull(server.springBootActuatorInfo().maven())
                        || isNull(server.springBootActuatorInfo().git())
                );
    }

    // TODO [YYL] fixme
//    @JsonIgnore
//    public boolean isAnyProblems(Team team) {
//        if (isNull(team)) {
//            return false;
//        }
//        return this.values.stream()
//                .filter(server -> team.equals(server.team()))
//                .anyMatch(server -> !server.ok());
//    }

    // TODO [YYL] fixme
//    @JsonIgnore
//    public boolean isAnyChanges(Team team) {
//        if (isNull(team)) {
//            return false;
//        }
//        return this.values.stream()
//                .filter(server -> team.equals(server.team()))
//                .anyMatch(Server::anyChanges);
//    }

    @JsonIgnore
    public Servers getServersFailure(Predicate<Server> predicate) {
        return new Servers(
                this.values.stream()
                        .filter(predicate)
                        .filter(server -> !server.ok())
                        .collect(Collectors.toList())
        );
    }

//    @JsonIgnore
//    public Servers getServers(Team team) {
//        return new Servers(
//                this.values.stream()
//                        .filter(app -> nonNull(team) && team.equals(app.team()))
//                        .collect(Collectors.toList())
//        );
//    }

    // ================================================================================================================
    // MESSAGES
    // ================================================================================================================
    public List<String> getStatus() {
        var messages = this.mappedValues.values().stream()
                .map(SlackMessageServerTable::new)
                .map(SlackMessageServerTable::getValue)
                .collect(Collectors.toList());
        messages.add(0, MessagesUtility.getTaskMessage(STATUS_SERVICE, this.anyProblems));
        return messages;
    }

    public List<String> getActuators() {
        List<String> messages = new ArrayList<>();
        messages.add(0, MessagesUtility.getTaskMessage(SPRING_BOOT_ACTUATORS_SERVICE, this.anyProblemsOnSpringBootActuators));
        messages.add(new SlackMessageServersSpringActuatorsTable(this.mappedActuatorsResponses).getValue());
        return messages;
    }

    public final List<String> getFS() {
        List<String> messages = new ArrayList<>();

        List<String> warningTables = new ArrayList<>();
        List<ServerFileSystemMetadata.FileSystemMetadataRow> successesRows = new ArrayList<>();

        this.values.forEach(server -> {
            if (server.fileSystemMetadataProblems()) {
                warningTables.add(new SlackMessageFileSystemTable(server).getValue());
            } else if (server.fileSystemMetadata().isAnyRows()) {
                successesRows.addAll(server.fileSystemMetadata().rows());
            }
        });

        if (!isEmpty(successesRows)) {
            successesRows.sort(PERCENTAGE_REVERSED);
            // WARNING: 25 is practical number is this case as max slack rows to wrap a message
            var partitionsSuccesses = Partitions.ofSize(successesRows, 25);
            partitionsSuccesses.forEach(chuckedMappedRows -> {
                var table = new SlackMessageFileSystemTable(chuckedMappedRows).getValue();
                messages.add(table);
            });
        }

        if (!isEmpty(warningTables)) {
            warningTables.add(0, MessagesUtility.getResponseWarnings());
            messages.addAll(warningTables);
        }

        if (isEmpty(successesRows) && isEmpty(warningTables)) {
            messages.add(SlackMessageFileSystemTable.getNoFsTable());
        }
        return messages;
    }
}
