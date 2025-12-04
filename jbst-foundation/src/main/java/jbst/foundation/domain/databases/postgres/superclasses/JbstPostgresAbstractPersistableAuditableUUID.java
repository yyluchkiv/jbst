package jbst.foundation.domain.databases.postgres.superclasses;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import jbst.foundation.domain.base.Username;
import jbst.foundation.domain.converters.JbstPostgresConverters;
import org.hibernate.annotations.UuidGenerator;
import org.springframework.data.domain.Persistable;
import org.springframework.lang.Nullable;

import java.util.UUID;

import static java.time.ZoneOffset.UTC;
import static java.util.Objects.isNull;
import static jbst.foundation.domain.constants.JbstConstants.DateTimeFormatters.DTF12;
import static jbst.foundation.domain.time.JbstTime.convert1;
import static jbst.foundation.domain.time.JbstTime.getCurrentTimestamp;

@MappedSuperclass
public abstract class JbstPostgresAbstractPersistableAuditableUUID implements Persistable<UUID> {
    @Id
    @UuidGenerator
    @Column(nullable = false, updatable = false)
    protected UUID id;

    @Basic
    @Convert(converter = JbstPostgresConverters.UsernameConverter.class)
    @Column(name = "created_by", nullable = false, updatable = false)
    protected Username createdBy;

    @Column(name = "created_at", nullable = false, updatable = false)
    protected long createdAt;

    @Basic
    @Convert(converter = JbstPostgresConverters.UsernameConverter.class)
    @Column(name = "updated_by", nullable = false)
    protected Username updatedBy;

    @Column(name = "updated_at", nullable = false)
    protected long updatedAt;

    protected JbstPostgresAbstractPersistableAuditableUUID() {
        // ignored, JPA-required
    }

    @Nullable
    @JsonIgnore
    @Override
    public UUID getId() {
        return this.id;
    }

    // DATAJPA-622
    @JsonIgnore
    @Transient
    @Override
    public boolean isNew() {
        return isNull(this.getId());
    }

    @JsonInclude
    @Transient
    public String getCreatedUTC() {
        return "%s @ %s".formatted(
                this.createdBy,
                DTF12.format(convert1(this.createdAt, UTC)) + " UTC"
        );
    }

    @JsonInclude
    @Transient
    public String getUpdatedUTC() {
        return "%s @ %s".formatted(
                this.updatedBy,
                DTF12.format(convert1(this.updatedAt, UTC)) + " UTC"
        );
    }

    @SuppressWarnings("unused")
    protected void update(Username username) {
        this.updatedBy = username;
        this.updatedAt = getCurrentTimestamp();
    }
}
