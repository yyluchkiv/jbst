package jbst.iam.domain.postgres.superclasses;

import jakarta.persistence.*;
import jbst.foundation.domain.base.Username;
import jbst.foundation.domain.converters.columns.PostgresUsernameConverter;
import org.hibernate.annotations.UuidGenerator;
import org.springframework.data.domain.Persistable;
import org.springframework.lang.Nullable;

import java.util.UUID;

import static java.util.Objects.isNull;
import static jbst.foundation.utilities.time.TimestampUtility.getCurrentTimestamp;

@MappedSuperclass
public abstract class PostgresDbAbstractPersistableAuditableUUID implements Persistable<UUID> {
    @Id
    @UuidGenerator
    @Column(nullable = false, updatable = false)
    protected UUID id;

    @Basic
    @Convert(converter = PostgresUsernameConverter.class)
    @Column(name = "created_by", nullable = false)
    protected Username createdBy;

    @Column(name = "created_at", nullable = false)
    protected long createdAt;

    @Column(name = "updated_at", nullable = false)
    protected long updatedAt;

    protected PostgresDbAbstractPersistableAuditableUUID(Username createdBy) {
        this.createdBy = createdBy;
        var currentTimestamp = getCurrentTimestamp();
        this.createdAt = currentTimestamp;
        this.updatedAt = currentTimestamp;
    }

    @Nullable
    @Override
    public UUID getId() {
        return this.id;
    }

    // DATAJPA-622
    @Transient
    @Override
    public boolean isNew() {
        return isNull(this.getId());
    }

    @SuppressWarnings("unused")
    protected void updated() {
        this.updatedAt = getCurrentTimestamp();
    }
}
