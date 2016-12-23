package com.mgl.demo.travelplanner.entity.support;

import java.io.Serializable;
import java.sql.Timestamp;
import java.time.Instant;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.Version;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.ColumnDefault;

@MappedSuperclass
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter(AccessLevel.PROTECTED)
@Setter(AccessLevel.PROTECTED)
@ToString
public abstract class BaseEntity<I extends Object> implements Serializable {

    private static final long serialVersionUID = 1L;

    protected static final String ENHANCED_SEQ = "enhanced-sequence";
    protected static final String ENHANCED_SEQ_INCREMENT = "10";

    @XmlElement
    protected abstract I getId();
    protected abstract void setId(I id);

    @Version
    @Column(nullable = false)
    @ColumnDefault("(now() at time zone 'utc')")
    private Timestamp updated;

    public Instant getLastUpdateTs() {
        return Instant.ofEpochMilli(updated.getTime());
    }

    protected void setLastUpdateTs(Instant lastUpdateTs) {
        this.updated = new Timestamp(lastUpdateTs.toEpochMilli());
    }

}
