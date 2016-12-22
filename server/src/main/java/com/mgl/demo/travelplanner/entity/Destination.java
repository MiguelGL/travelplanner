package com.mgl.demo.travelplanner.entity;

import com.mgl.demo.travelplanner.entity.support.BaseEntity;

import static org.hibernate.id.enhanced.SequenceStyleGenerator.INCREMENT_PARAM;
import static org.hibernate.id.enhanced.SequenceStyleGenerator.SEQUENCE_PARAM;

import java.util.Objects;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.validator.constraints.NotBlank;

@Entity
@Table(
        uniqueConstraints = {
            @UniqueConstraint(name = "destination__name_uidx", columnNames = "name")
        }
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter(AccessLevel.PROTECTED)
@Setter(AccessLevel.PROTECTED)
@ToString(callSuper = true, exclude = {"trips"})
@EqualsAndHashCode(callSuper = false, of = "id")
public class Destination extends BaseEntity<Long> {

    private static final long serialVersionUID = 1L;

    private static final int DESTINATION_MIX_LEN = 1;
    private static final int DESTINATION_MAX_LEN = 256;

    @Id
    @Column(nullable = false)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "destination_id_gen")
    @GenericGenerator(name = "destination_id_gen", strategy = ENHANCED_SEQ,
            parameters = {
                @Parameter(name = SEQUENCE_PARAM, value = "tp_destination_id_seq"),
                @Parameter(name = INCREMENT_PARAM, value = ENHANCED_SEQ_INCREMENT),
            })
    @ColumnDefault("nextval('tp_destination_id_seq')")
    private Long id;

    @NotNull
    @NotBlank
    @Size(min = DESTINATION_MIX_LEN, max = DESTINATION_MAX_LEN)
    @Column(nullable = false, length = DESTINATION_MAX_LEN)
    private String name;

    @OneToMany(mappedBy = "destination", orphanRemoval = true, cascade = {CascadeType.REMOVE})
    private Set<Trip> trips;

    public Destination(String name) {
        this.name = Objects.requireNonNull(name, "name");
    }

}
