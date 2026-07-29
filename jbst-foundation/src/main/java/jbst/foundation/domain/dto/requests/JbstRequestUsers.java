package jbst.foundation.domain.dto.requests;

import jbst.foundation.domain.base.Email;
import jbst.foundation.domain.base.Username;
import jbst.foundation.domain.databases.postgres.entities.JbstPostgresUser;
import org.jetbrains.annotations.Nullable;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;

import static java.util.Objects.nonNull;
import static jbst.foundation.domain.databases.postgres.entities.JbstPostgresUserSpecs.*;

public record JbstRequestUsers(
        @Nullable Username username,
        @Nullable Email email,
        @Nullable String name
) {

    public Specification<JbstPostgresUser> toSpecification() {
        var specifications = new ArrayList<Specification<JbstPostgresUser>>();

        if (nonNull(this.username)) {
            specifications.add(hasUsername(this.username));
        }
        if (nonNull(this.email)) {
            specifications.add(hasEmail(this.email));
        }
        if (nonNull(this.name)) {
            specifications.add(hasName(this.name));
        }

        // empty filter must match all users — anyOf(empty) matches none
        return specifications.isEmpty() ? Specification.unrestricted() : Specification.anyOf(specifications);
    }

}
