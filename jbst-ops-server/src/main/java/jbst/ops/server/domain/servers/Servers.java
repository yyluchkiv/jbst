package jbst.ops.server.domain.servers;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jbst.foundation.domain.base.ServerName;
import jbst.foundation.domain.tuples.Tuple2;
import jbst.foundation.feigns.spring.SpringBootClient;
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
import static java.util.Objects.nonNull;
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

    public Servers() {
        this(
                new ArrayList<>()
        );
    }

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

    @JsonIgnore
    public boolean isAnyProblems(Team team) {
        if (isNull(team)) {
            return false;
        }
        return this.values.stream()
                .filter(server -> team.equals(server.team()))
                .anyMatch(server -> !server.ok());
    }

    @JsonIgnore
    public boolean isAnyChanges(Team team) {
        if (isNull(team)) {
            return false;
        }
        return this.values.stream()
                .filter(server -> team.equals(server.team()))
                .anyMatch(Server::anyChanges);
    }

    @JsonIgnore
    public Servers getServersFailure(Predicate<Server> predicate) {
        return new Servers(
                this.values.stream()
                        .filter(predicate)
                        .filter(server -> !server.ok())
                        .collect(Collectors.toList())
        );
    }

    @JsonIgnore
    public Servers getServers(Team team) {
        return new Servers(
                this.values.stream()
                        .filter(app -> nonNull(team) && team.equals(app.team()))
                        .collect(Collectors.toList())
        );
    }
}
