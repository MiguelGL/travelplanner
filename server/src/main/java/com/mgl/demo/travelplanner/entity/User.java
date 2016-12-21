package com.mgl.demo.travelplanner.entity;

import com.mgl.demo.travelplanner.entity.support.BaseEntity;

import static org.hibernate.id.enhanced.SequenceStyleGenerator.INCREMENT_PARAM;
import static org.hibernate.id.enhanced.SequenceStyleGenerator.SEQUENCE_PARAM;

import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
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
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotBlank;

@Entity
@Table
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter(AccessLevel.PROTECTED)
@Setter(AccessLevel.PROTECTED)
@ToString(callSuper = true, exclude = {"trips"})
@EqualsAndHashCode(callSuper = false, of = "id")
public class User extends BaseEntity<Long> {

    private static final long serialVersionUID = 1L;

    private static final String NO_LAST_NAME = "";

    private static final int EMAIL_MIN_LEN = 3;
    private static final int EMAIL_MAX_LEN = 64;

    private static final int FIRST_NAME_MIN_LEN = 1;
    private static final int FIRST_NAME_MAX_LEN = 64;

    private static final int LAST_NAME_MIN_LEN = 0;
    private static final int LAST_NAME_MAX_LEN = 128;

    @Id
    @Column(nullable = false)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_id_gen")
    @GenericGenerator(name = "user_id_gen", strategy = ENHANCED_SEQ,
            parameters = {
                @Parameter(name = SEQUENCE_PARAM, value = "user_id_seq"),
                @Parameter(name = INCREMENT_PARAM, value = ENHANCED_SEQ_INCREMENT),
            })
    @ColumnDefault("nextval('user_id_seq')")
    private Long id;

    @NotNull
    @NotBlank
    @Email(regexp = ".*")
    @Size(min = EMAIL_MIN_LEN, max = EMAIL_MAX_LEN)
    @Column(nullable = false, length = EMAIL_MAX_LEN)
    private String email;

    @NotNull
    @NotBlank
    @Size(min = FIRST_NAME_MIN_LEN, max = FIRST_NAME_MAX_LEN)
    @Column(nullable = false, length = FIRST_NAME_MAX_LEN)
    private String firstName;

    @NotNull
    @Size(min = LAST_NAME_MIN_LEN, max = LAST_NAME_MAX_LEN)
    @Column(nullable = false, length = LAST_NAME_MAX_LEN)
    @ColumnDefault("''")
    private String lastName = NO_LAST_NAME;

    @OneToMany(mappedBy = "user", orphanRemoval = true, cascade = {CascadeType.REMOVE})
    private Set<Trip> trips;

    public User(String email, String firstName, String lastName) {
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public User(String email, String firstName) {
        this(email, firstName, "");
    }

    public String getFullName() {
        StringBuilder sb = new StringBuilder(getFirstName());
        if (!getLastName().isEmpty()) {
            sb.append(getLastName());
        }
        return sb.toString();
    }

}
