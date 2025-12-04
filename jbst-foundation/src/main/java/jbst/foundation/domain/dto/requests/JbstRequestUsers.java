package jbst.foundation.domain.dto.requests;

import jbst.foundation.domain.base.Email;
import jbst.foundation.domain.base.Username;
import jbst.foundation.domain.databases.postgres.entities.JbstPostgresUser;
import org.jetbrains.annotations.Nullable;
import org.springframework.data.jpa.domain.Specification;

import static java.util.Objects.nonNull;
import static jbst.foundation.domain.databases.postgres.entities.JbstPostgresUserSpecs.*;

public record JbstRequestUsers(
        @Nullable Username username,
        @Nullable Email email,
        @Nullable String name
) {

    public Specification<JbstPostgresUser> toSpecification() {
        var specification = Specification.<JbstPostgresUser>where(null);

        if (nonNull(this.username)) {
            specification = specification.or(hasUsername(this.username));
        }
        if (nonNull(this.email)) {
            specification = specification.or(hasEmail(this.email));
        }
        if (nonNull(this.name)) {
            specification = specification.or(hasName(this.name));
        }

        return specification;
    }

}
