package jbst.iam.domain.postgres.superclasses;

import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Transient;
import org.hibernate.annotations.UuidGenerator;
import org.jetbrains.annotations.Nullable;
import org.springframework.data.domain.Persistable;

import java.util.UUID;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@MappedSuperclass
public abstract class PostgresDbAbstractPersistable0 implements Persistable<UUID> {
    @Id
    @UuidGenerator
    @Column(nullable = false, updatable = false)
    protected UUID id;

    protected PostgresDbAbstractPersistable0() {
        // ignored
    }

    @Nullable
    public String getPlainId() {
        return nonNull(this.id) ? this.id.toString() : null;
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
}
