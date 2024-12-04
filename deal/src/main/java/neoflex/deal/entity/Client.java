package neoflex.deal.entity;

import jakarta.persistence.*;
import lombok.*;
import neoflex.deal.converter.EmploymentConverter;
import neoflex.deal.converter.PassportConverter;
import neoflex.deal.enums.Gender;
import neoflex.deal.enums.MaritalStatus;
import org.hibernate.annotations.ColumnTransformer;

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

    @Convert(converter = PassportConverter.class)
    @Column(columnDefinition = "jsonb")
    @ColumnTransformer(read = "passport::jsonb", write = "?::jsonb")
    private Passport passport;

    @Convert(converter = EmploymentConverter.class)
    @Column(columnDefinition = "jsonb")
    @ColumnTransformer(read = "employment::jsonb", write = "?::jsonb")
    private Employment employment;

    private String accountNumber;
}
