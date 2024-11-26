package neoflex.deal.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import neoflex.deal.entity.enums.Gender;
import neoflex.deal.entity.enums.MaritalStatus;
import java.util.UUID;

import java.time.LocalDate;

/**
 * Сущность, представляющая клиента.
 */
@Entity
@Getter
@Setter
public class Client {
    @Id
    @GeneratedValue
    private UUID clientId;
    private String lastName;
    private String firstName;
    private String middleName;
    private LocalDate birthDate;
    private String email;
    @Enumerated(EnumType.STRING)
    private Gender gender;
    @Enumerated(EnumType.STRING)
    private MaritalStatus maritalStatus;
    private int dependentAmount;
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "passport_id", referencedColumnName = "passportUid")
    private Passport passport;
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "employment_id", referencedColumnName = "employmentUid")
    private Employment employment;
    private String accountNumber;
}
