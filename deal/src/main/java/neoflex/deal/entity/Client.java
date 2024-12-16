package neoflex.deal.entity;

import jakarta.persistence.*;
import lombok.*;

import neoflex.enums.Gender;
import neoflex.enums.MaritalStatus;
import org.hibernate.annotations.ColumnTransformer;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.UUID;
import java.time.LocalDate;

/**
 * Сущность, представляющая клиента.
 */
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "client")
public class Client {
    @Id
    @GeneratedValue
    @Column(name = "client_id")
    private UUID clientId;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "middle_name")
    private String middleName;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    @Column(name = "email")
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender")
    private Gender gender;

    @Enumerated(EnumType.STRING)
    @Column(name = "marital_status")
    private MaritalStatus maritalStatus;

    @Column(name = "dependent_amount")
    private int dependentAmount;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "passport", columnDefinition = "jsonb")
    private Passport passport;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "employment", columnDefinition = "jsonb")
    private Employment employment;

    @Column(name = "account_number")
    private String accountNumber;
}