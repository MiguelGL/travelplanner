package com.mgl.demo.travelplanner.entity;

import static com.google.common.base.MoreObjects.firstNonNull;
import static org.hibernate.id.enhanced.SequenceStyleGenerator.INCREMENT_PARAM;
import static org.hibernate.id.enhanced.SequenceStyleGenerator.SEQUENCE_PARAM;

import java.time.LocalDate;
import java.util.Objects;
import java.util.Set;

import javax.annotation.Nullable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.ValidationException;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableSet;
import com.mgl.demo.travelplanner.entity.support.BaseEntity;
import com.mgl.demo.travelplanner.entity.support.LocalDateAdapter;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

@Entity
@Table(
        indexes = {
            @Index(name = "trip__start_date_idx", columnList = "startDate"),
            @Index(name = "trip__end_date_idx", columnList = "endDate")
        },
        uniqueConstraints = {
            @UniqueConstraint(name = "trip__user__start_date_uidx", columnNames = {"user_id", "startDate"}),
            @UniqueConstraint(name = "trip__user__end_date_uidx", columnNames = {"user_id", "endDate"}),
        }
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter(AccessLevel.PROTECTED)
@Setter(AccessLevel.PROTECTED)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false, of = "id")
public class Trip extends BaseEntity<Long> {

    private static final String NO_COMMENT = "";

    private static final long serialVersionUID = 1L;

    public static final int COMMENT_MIX_LEN = 0;
    public static final int COMMENT_MAX_LEN = 512;

    @Id
    @Column(nullable = false)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "trip_id_gen")
    @GenericGenerator(name = "trip_id_gen", strategy = ENHANCED_SEQ,
            parameters = {
                @Parameter(name = SEQUENCE_PARAM, value = "trip_id_seq"),
                @Parameter(name = INCREMENT_PARAM, value = ENHANCED_SEQ_INCREMENT),
            })
    @ColumnDefault("nextval('tp_trip_id_seq')")
    private Long id;

    @NotNull
    @ManyToOne(optional = false)
    @JoinColumn(nullable = false)
    @XmlTransient
    private User user;

    @NotNull
    @ManyToOne(optional = false)
    @JoinColumn(nullable = false)
    @XmlTransient
    private Destination destination;

    @NotNull
    // @Temporal(TemporalType.DATE)
    @Column(nullable = false)
    @XmlJavaTypeAdapter(LocalDateAdapter.class)
    private LocalDate startDate;

    @NotNull
    // @Temporal(TemporalType.DATE)
    @Column(nullable = false)
    @XmlJavaTypeAdapter(LocalDateAdapter.class)
    private LocalDate endDate;

    @NotNull
    @Size(min = COMMENT_MIX_LEN, max = COMMENT_MAX_LEN)
    @Column(nullable = false, length = COMMENT_MAX_LEN)
    @ColumnDefault("''")
    private String comment = NO_COMMENT;

    public Trip(User user, Destination destination, LocalDate startDate, LocalDate endDate, String comment) {
        this.user = Objects.requireNonNull(user, "user");
        this.destination = Objects.requireNonNull(destination, "destination");
        this.startDate = Objects.requireNonNull(startDate, "startDate");
        this.endDate = Objects.requireNonNull(endDate, "endDate");
        this.comment = Objects.requireNonNull(comment, "comment");
    }

    public Trip(User user, Destination destination, LocalDate startDate, LocalDate endDate) {
        this(user, destination, startDate, endDate, NO_COMMENT);
    }

    public Trip newWithNullables(
            Destination destination,
            LocalDate startDate,
            LocalDate endDate,
            String comment) {
        Trip trip = new Trip();
        trip.setUser(getUser());
        trip.setDestination(destination);
        trip.setStartDate(startDate);
        trip.setEndDate(endDate);
        trip.setComment(comment);
        return trip;
    }

    public boolean hasComment() {
        return NO_COMMENT.equals(getComment());
    }

    public static void validateDates(LocalDate startDate, LocalDate endDate) {
        if (endDate.isBefore(startDate)) {
            throw new ValidationException(String.format(
                    "Start date '%s' must be before or equal to end date '%s'",
                    startDate, endDate));
        }
    }

    public void validateDates() {
        validateDates(getStartDate(), getEndDate());
    }

    @PrePersist @PreUpdate
    public void prePersist() {
        validateDates();
    }

    @XmlElement
    String getUserEmail() {
        return getUser().getEmail();
    }

    @XmlElement
    String getDestinationName() {
        return getDestination().getName();
    }

    public static String ensureComment(@Nullable String comment) {
        return MoreObjects.firstNonNull(comment, NO_COMMENT);
    }

    public boolean isForUser(User forUser) {
        return getUser().equals(forUser);
    }

    public void prepareForUpdate(Trip tripTemplate) {
        super.prepareForUpdate(tripTemplate);
        // user: not updatable
        setDestination(firstNonNull(tripTemplate.getDestination(), getDestination()));
        setStartDate(firstNonNull(tripTemplate.getStartDate(), getStartDate()));
        setEndDate(firstNonNull(tripTemplate.getEndDate(), getEndDate()));
        setComment(firstNonNull(tripTemplate.getComment(), getComment()));
    }

    @FunctionalInterface
    public static interface OverlappingTripsChecker {
        boolean checkOverlappingTrips(
                User user,
                LocalDate startDate, LocalDate endDate,
                Set<Trip> excludedTrips);
    }

    public boolean checkOverlappingTripsForUpdate(OverlappingTripsChecker checker) {
        return checker.checkOverlappingTrips(
                getUser(), getStartDate(), getEndDate(), ImmutableSet.of(this));
    }

}
