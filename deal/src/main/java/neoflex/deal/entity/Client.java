package neoflex.deal.entity;

import jakarta.persistence.*;
import lombok.*;
import neoflex.deal.enums.Gender;
import neoflex.deal.enums.MaritalStatus;
import java.util.UUID;

import java.time.LocalDate;

/**
 * Сущность, представляющая клиента.
 */
@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
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
