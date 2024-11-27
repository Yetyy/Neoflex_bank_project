package neoflex.deal.service;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import neoflex.deal.dto.*;
import neoflex.deal.entity.*;
import neoflex.deal.enums.ApplicationStatus;
import neoflex.deal.enums.ChangeType;
import neoflex.deal.enums.CreditStatus;
import neoflex.deal.repository.*;
import neoflex.deal.util.DtoConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import jakarta.validation.Valid;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Сервис для обработки бизнес-логики, связанной с кредитными заявками.
 */
@Service
public class DealService {

    private static final Logger logger = LoggerFactory.getLogger(DealService.class);

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private StatementRepository statementRepository;

    @Autowired
    private CreditRepository creditRepository;

    @Autowired
    private StatusHistoryRepository statusHistoryRepository;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private Validator validator;

    /**
     * Рассчитывает возможные условия кредита на основе данных заявки.
     *
     * @param request объект с данными заявки на кредит
     * @return список предложений по кредиту
     */
    public List<LoanOfferDto> calculateLoanOffers(@Valid LoanStatementRequestDto request) {
        Set<ConstraintViolation<LoanStatementRequestDto>> violations = validator.validate(request);
        if (!violations.isEmpty()) {
            throw new IllegalArgumentException("Invalid loan statement request: " + violations);
        }

        logger.info("Расчет возможных условий кредита для заявки: {}", request);

        // Создание и сохранение клиента
        Client client = Client.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .middleName(request.getMiddleName())
                .birthDate(request.getBirthDate())
                .email(request.getEmail())
                .gender(request.getGender())
                .maritalStatus(request.getMaritalStatus())
                .dependentAmount(request.getDependentAmount())
                .build();
        clientRepository.save(client);

        // Создание и сохранение заявки
        Statement statement = Statement.builder()
                .client(client)
                .status(ApplicationStatus.PREAPPROVAL)
                .build();
        statementRepository.save(statement);

        // Отправка запроса в МС Калькулятор
        List<LoanOfferDto> loanOffers = restTemplate.postForObject("http://calculator/offers", request, List.class);

        // Присвоение id заявки каждому предложению
        loanOffers.forEach(offer -> offer.setStatementId(statement.getStatementId()));

        return loanOffers;
    }

    /**
     * Выбирает одно из предложений по кредиту.
     *
     * @param offer объект с данными выбранного предложения
     */
    public void selectLoanOffer(LoanOfferDto offer) {
        logger.info("Выбор предложения по кредиту: {}", offer);

        // Получение заявки из БД
        Statement statement = statementRepository.findById(offer.getStatementId()).orElseThrow();

        // Обновление статуса заявки
        statement.setStatus(ApplicationStatus.APPROVED);
        statement.setAppliedOffer(offer.toString());

        // Обновление истории статусов
        StatusHistory statusHistory = StatusHistory.builder()
                .status(ApplicationStatus.APPROVED.name())
                .time(LocalDateTime.now())
                .changeType(ChangeType.MANUAL)
                .build();
        statusHistoryRepository.save(statusHistory);

        // Сохранение заявки
        statementRepository.save(statement);
    }

}
