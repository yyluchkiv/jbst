package jbst.iam.server.postgres.domain.db;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import jbst.foundation.domain.base.Username;
import jbst.foundation.domain.converters.columns.PostgresUsernameConverter;

import static jbst.iam.server.postgres.constants.TablesConstants.ANYTHING;
import static jbst.foundation.utilities.random.RandomUtility.randomStringLetterOrNumbersOnly;

// Lombok
@NoArgsConstructor
@Getter
@EqualsAndHashCode
@ToString
// JPA
@Entity
@Table(name = ANYTHING)
public class PostgresDbAnything {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(length = 36, nullable = false, updatable = false)
    private String id;

    @Convert(converter = PostgresUsernameConverter.class)
    @Column(nullable = false)
    private Username username;

    @Column
    private String value;

    public PostgresDbAnything(Username username) {
        this.username = username;
        this.value = randomStringLetterOrNumbersOnly(20);
    }
}
