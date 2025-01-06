package jbst.ops.server.domain.authorities;

import jbst.ops.server.domain.servers.Team;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

import static jbst.ops.server.domain.authorities.Permission.FOUNDERS;
import static jbst.ops.server.domain.authorities.Permission.TEAM;
import static org.springframework.util.CollectionUtils.isEmpty;

// Lombok
@Getter
@EqualsAndHashCode
@ToString
public class Permissions {
    // section: permission - YES
    private final List<Permission> accesses;
    // section: by permissions
    private Team team;

    public Permissions() {
        this.accesses = new ArrayList<>();
    }

    public void addFoundersPermission() {
        this.accesses.add(FOUNDERS);
    }

    public void addTeamPermission(Team team) {
        this.accesses.add(TEAM);
        this.team = team;
    }

    public boolean containsFounders() {
        return !isEmpty(this.accesses) && this.accesses.contains(FOUNDERS);
    }

    public boolean containsTeam() {
        return !isEmpty(this.accesses) && this.accesses.contains(TEAM);
    }
}
