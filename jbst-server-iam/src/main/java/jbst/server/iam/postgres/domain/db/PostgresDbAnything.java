package jbst.server.iam.postgres.domain.db;

import jakarta.persistence.*;
import jbst.foundation.domain.base.Username;
import jbst.foundation.domain.converters.PostgresConverters;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import static jbst.foundation.utilities.random.RandomUtility.randomStringLetterOrNumbersOnly;
import static jbst.server.iam.postgres.constants.TablesConstants.ANYTHING;

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

    @Convert(converter = PostgresConverters.UsernameConverter.class)
    @Column(nullable = false)
    private Username username;

    @Column
    private String value;

    public PostgresDbAnything(Username username) {
        this.username = username;
        this.value = randomStringLetterOrNumbersOnly(20);
    }
}
