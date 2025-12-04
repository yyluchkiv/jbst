package jbst.foundation.domain.databases.postgres.entities;

import jbst.foundation.domain.base.Email;
import jbst.foundation.domain.base.Username;
import lombok.experimental.UtilityClass;
import org.springframework.data.jpa.domain.Specification;

@UtilityClass
public class JbstPostgresUserSpecs {

    public static Specification<JbstPostgresUser> hasUsername(Username username) {
        return (root, query, builder) ->
                builder.equal(root.get(JbstPostgresUser_.username), username);
    }

    public static Specification<JbstPostgresUser> hasEmail(Email email) {
        return (root, query, builder) ->
                builder.equal(root.get(JbstPostgresUser_.email), email);
    }

    public static Specification<JbstPostgresUser> hasName(String name) {
        return (root, query, builder) ->
                builder.equal(root.get(JbstPostgresUser_.name), name);
    }

}
