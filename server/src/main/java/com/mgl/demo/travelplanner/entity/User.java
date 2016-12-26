package com.mgl.demo.travelplanner.entity;

import static com.google.common.base.MoreObjects.firstNonNull;
import static com.mgl.demo.travelplanner.entity.Role.ROLE_MAX_LEN;
import static lombok.AccessLevel.PUBLIC;

import com.mgl.demo.travelplanner.entity.support.BaseEntity;

import static org.hibernate.id.enhanced.SequenceStyleGenerator.INCREMENT_PARAM;
import static org.hibernate.id.enhanced.SequenceStyleGenerator.SEQUENCE_PARAM;

import java.util.Base64;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Pattern;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.ValidationException;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import com.google.common.base.Charsets;
import com.google.common.base.MoreObjects;
import com.google.common.base.Strings;
import com.google.common.hash.HashCode;
import com.google.common.hash.Hashing;
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
@Table(
        indexes = {
            @Index(name = "user__first_name_idx", columnList = "firstName"),
            @Index(name = "user__last_name_idx", columnList = "lastName")
        },
        uniqueConstraints = {
            @UniqueConstraint(name = "user__email_uidx", columnNames = {"email"})
        }
)
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
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

    private static final String VALID_EMAIL_REGEX = ".+@.+";
    private static final Pattern VALID_EMAIL_PATTERN = Pattern.compile(VALID_EMAIL_REGEX);

    // base64(sha256(admin)) -> jGl25bVBBBW96Qi9Te4V37Fnqchz/Eu4qB9vKrRIqRg=
    private static final int PASSWORD_MIN_LEN = 44;
    private static final int PASSWORD_MAX_LEN = 44;

    private static final int PLAIN_PASSWORD_MIN_LEN = 1;
    private static final int PLAIN_PASSWORD_MAX_LEN = 48;

    private static final String PLAIN_VALID_PASSWORD_REGEX = "^[0-9a-zA-Z\\_\\-]+$";
    private static final Pattern PLAIN_VALID_PASSWORD_PATTERN = Pattern.compile(PLAIN_VALID_PASSWORD_REGEX);

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
    @ColumnDefault("nextval('tp_user_id_seq')")
    private Long id;

    @NotNull
    @NotBlank
    @Email(regexp = VALID_EMAIL_REGEX)
    @Size(min = EMAIL_MIN_LEN, max = EMAIL_MAX_LEN)
    @Column(nullable = false, length = EMAIL_MAX_LEN)
    private String email;

    @NotNull
    @NotBlank
    @Size(min = PASSWORD_MIN_LEN, max = PASSWORD_MAX_LEN)
    @Column(nullable = false, length = PASSWORD_MAX_LEN)
    @XmlTransient
    private @Setter(PUBLIC) String password;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = ROLE_MAX_LEN, name = "user_role") // 'role' SQL reserved
    private Role role;

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
    @XmlTransient
    private Set<Trip> trips;

    public static User buildNewUser(User userTemplate) {
        return new User(
                Strings.nullToEmpty(userTemplate.getEmail()),
                Strings.nullToEmpty(userTemplate.getPassword()),
                MoreObjects.firstNonNull(userTemplate.getRole(), Role.REGULAR_USER),
                Strings.nullToEmpty(userTemplate.getFirstName()),
                Strings.nullToEmpty(userTemplate.getLastName()));
    }

    public User(String email, String password, Role role, String firstName, String lastName) {
        this.email = Objects.requireNonNull(email, "email");
        this.password = Objects.requireNonNull(password, "password");
        this.role = Objects.requireNonNull(role, "role");
        this.firstName = Objects.requireNonNull(firstName, "firstName");
        this.lastName = Objects.requireNonNull(lastName, "lastName");
    }

    public User(String email, String password, Role role, String firstName) {
        this(email, password, role, firstName, NO_LAST_NAME);
    }

    public void prepareForUpdate(User userTemplate) {
        super.prepareForUpdate(userTemplate);
        // email: not updatable
        setPassword(firstNonNull(userTemplate.getPassword(), getPassword()));
        // role: not updatable
        setFirstName(firstNonNull(userTemplate.getFirstName(), getFirstName()));
        setLastName(firstNonNull(userTemplate.getLastName(), getLastName()));
    }

    public static boolean isValidEmail(String email) {
        return VALID_EMAIL_PATTERN.matcher(Strings.nullToEmpty(email)).matches();
    }

    public static boolean isValidPlainPassword(String plainPassword) {
        return plainPassword.length() >= PLAIN_PASSWORD_MIN_LEN
                && plainPassword.length() <= PLAIN_PASSWORD_MAX_LEN
                && PLAIN_VALID_PASSWORD_PATTERN.matcher(Strings.nullToEmpty(plainPassword)).matches();
    }

    public static String validateAndEncryptPlainPassword(String plainPassword) {
        if (!isValidPlainPassword(plainPassword)) {
            throw new ValidationException("Invalid password");
        }
        return encryptPlainPassword(plainPassword);
    }

    public static String encryptPlainPassword(String plainPassword) {
        HashCode hashedPassword = Hashing.sha256().hashString(plainPassword, Charsets.UTF_8);
        String encodedHashedPassword = Base64.getEncoder().encodeToString(hashedPassword.asBytes());
        return encodedHashedPassword;
    }

    @XmlElement
    public String getFullName() {
        StringBuilder sb = new StringBuilder(getFirstName());
        if (!NO_LAST_NAME.equals(getLastName())) {
            sb.append(getLastName());
        }
        return sb.toString();
    }

    public boolean hasAllUserTripsAccess() {
        return getRole().hasAllUserTripsAccess();
    }

}
