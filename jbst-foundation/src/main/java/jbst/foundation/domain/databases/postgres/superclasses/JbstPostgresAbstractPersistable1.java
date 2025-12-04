package jbst.foundation.domain.databases.postgres.superclasses;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;

import static jbst.foundation.domain.time.JbstTime.getCurrentTimestamp;

@Getter
@Setter
@MappedSuperclass
public abstract class JbstPostgresAbstractPersistable1 extends JbstPostgresAbstractPersistable0 {

    @Column(name = "created_at", nullable = false)
    protected long createdAt;

    @Column(name = "updated_at", nullable = false)
    protected long updatedAt;

    protected JbstPostgresAbstractPersistable1() {
        super();
        var currentTimestamp = getCurrentTimestamp();
        this.createdAt = currentTimestamp;
        this.updatedAt = currentTimestamp;
    }

    @SuppressWarnings("unused")
    protected void updated() {
        this.updatedAt = getCurrentTimestamp();
    }
}
