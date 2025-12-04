package jbst.foundation.domain.databases.postgres.superclasses;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.MappedSuperclass;
import jbst.foundation.domain.base.Username;
import jbst.foundation.domain.converters.JbstPostgresConverters;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import static jbst.foundation.domain.time.JbstTime.getCurrentTimestamp;

@Getter
@Setter
@NoArgsConstructor
@MappedSuperclass
public abstract class PostgresDbAbstractPersistable2 extends PostgresDbAbstractPersistable0 {

    @Convert(converter = JbstPostgresConverters.UsernameConverter.class)
    @Column(name = "created_by", nullable = false)
    protected Username createdBy;

    @Column(name = "created_at", nullable = false)
    protected long createdAt;

    @SuppressWarnings("unused")
    protected PostgresDbAbstractPersistable2(Username createdBy) {
        this.createdBy = createdBy;
        this.createdAt = getCurrentTimestamp();
    }
}
