package jbst.foundation.domain.dto.requests;

import jbst.foundation.domain.base.Email;
import jbst.foundation.domain.base.Username;
import jbst.foundation.domain.databases.postgres.entities.PostgresDbUser;
import org.jetbrains.annotations.Nullable;
import org.springframework.data.jpa.domain.Specification;

import static java.util.Objects.nonNull;
import static jbst.foundation.domain.databases.postgres.entities.PostgresDbUserSpecs.*;

public record RequestUsers(
        @Nullable Username username,
        @Nullable Email email,
        @Nullable String name
) {

    public Specification<PostgresDbUser> toSpecification() {
        var specification = Specification.<PostgresDbUser>where(null);

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
