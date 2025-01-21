package neoflex.deal.mapper;

import neoflex.dto.ScoringDataDto;
import neoflex.deal.entity.Credit;
import neoflex.deal.entity.Client;
import neoflex.deal.entity.Statement;
import neoflex.dto.FinishRegistrationRequestDto;

/**
 * Маппер для преобразования данных заявки и запроса на завершение регистрации в DTO для скоринга.
 */
public class ScoringDataMapper {

    private ScoringDataMapper() {
    }

    /**
     * Преобразует данные заявки и запроса на завершение регистрации в DTO для скоринга.
     *
     * @param statement Заявка, содержащая данные клиента и кредита.
     * @param request   Запрос на завершение регистрации, содержащий данные о занятости.
     * @return DTO для скоринга, содержащий все необходимые данные для расчета кредита.
     */
    public static ScoringDataDto toScoringDataDto(Statement statement, FinishRegistrationRequestDto request) {
        Client client = statement.getClient();
        Credit credit = statement.getCredit();

        return ScoringDataDto.builder()
                .amount(credit.getAmount())
                .term(credit.getTerm())
                .firstName(client.getFirstName())
                .lastName(client.getLastName())
                .middleName(client.getMiddleName())
                .gender(client.getGender())
                .birthdate(client.getBirthDate())
                .passportSeries(client.getPassport().getSeries())
                .passportNumber(client.getPassport().getNumber())
                .passportIssueDate(client.getPassport().getIssueDate())
                .maritalStatus(client.getMaritalStatus())
                .dependentAmount(client.getDependentAmount())
                .employment(request.getEmployment())
                .accountNumber(client.getAccountNumber())
                .isInsuranceEnabled(credit.isInsuranceEnabled())
                .isSalaryClient(credit.isSalaryClient())
                .build();
    }
}
