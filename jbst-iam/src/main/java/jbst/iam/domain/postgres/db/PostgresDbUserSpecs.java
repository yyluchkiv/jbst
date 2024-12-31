package jbst.iam.domain.postgres.db;

import jbst.foundation.domain.base.Email;
import jbst.foundation.domain.base.Username;
import lombok.experimental.UtilityClass;
import org.springframework.data.jpa.domain.Specification;

@UtilityClass
public class PostgresDbUserSpecs {

    public static Specification<PostgresDbUser> hasUsername(Username username) {
        return (root, query, builder) ->
                builder.equal(root.get(PostgresDbUser_.username), username);
    }

    public static Specification<PostgresDbUser> hasEmail(Email email) {
        return (root, query, builder) ->
                builder.equal(root.get(PostgresDbUser_.email), email);
    }

    public static Specification<PostgresDbUser> hasName(String name) {
        return (root, query, builder) ->
                builder.equal(root.get(PostgresDbUser_.name), name);
    }

}
