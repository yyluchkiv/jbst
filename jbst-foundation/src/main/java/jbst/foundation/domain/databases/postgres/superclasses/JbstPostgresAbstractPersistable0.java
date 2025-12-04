package jbst.foundation.domain.databases.postgres.superclasses;

import jakarta.persistence.*;
import org.springframework.data.domain.Persistable;
import org.springframework.lang.Nullable;

import static java.util.Objects.isNull;

@MappedSuperclass
public abstract class JbstPostgresAbstractPersistable0 implements Persistable<String> {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(length = 36, nullable = false, updatable = false)
    protected String id;

    protected JbstPostgresAbstractPersistable0() {
        // ignored
    }

    @Nullable
    @Override
    public String getId() {
        return this.id;
    }

    // DATAJPA-622
    @Transient
    @Override
    public boolean isNew() {
        return isNull(this.getId());
    }
}
