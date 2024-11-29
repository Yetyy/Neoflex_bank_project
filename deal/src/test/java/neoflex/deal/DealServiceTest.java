package neoflex.deal;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import neoflex.deal.dto.*;
import neoflex.deal.entity.*;
import neoflex.deal.enums.*;
import neoflex.deal.repository.*;
import neoflex.deal.service.DealService;
import neoflex.deal.util.DtoConverter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DealServiceTest {

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private StatementRepository statementRepository;

    @Mock
    private CreditRepository creditRepository;

    @Mock
    private StatusHistoryRepository statusHistoryRepository;

    @Mock
    private WebClient webClient;

    @Mock
    private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;

    @Mock
    private WebClient.RequestHeadersSpec requestHeadersSpec;

    @Mock
    private WebClient.RequestBodySpec requestBodySpec;

    @Mock
    private WebClient.RequestBodyUriSpec requestBodyUriSpec;

    @Mock
    private WebClient.ResponseSpec responseSpec;

    @Mock
    private Validator validator;

    @InjectMocks
    private DealService dealService;

    private LoanStatementRequestDto loanStatementRequestDto;
    private LoanOfferDto loanOfferDto;
    private FinishRegistrationRequestDto finishRegistrationRequestDto;
    private Client client;
    private Statement statement;
    private Credit credit;
    private ScoringDataDto scoringDataDto;
    private CreditDto creditDto;

    @BeforeEach
    public void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
        loanStatementRequestDto = LoanStatementRequestDto.builder()
                .firstName("John")
                .lastName("Doe")
                .middleName("Smith")
                .birthDate(LocalDate.of(1990, 1, 1))
                .email("john.doe@example.com")
                .gender(Gender.MALE)
                .maritalStatus(MaritalStatus.SINGLE)
                .dependentAmount(0)
                .amount(BigDecimal.valueOf(20000))
                .term(12)
                .passportSeries("1234")
                .passportNumber("567890")
                .build();

        loanOfferDto = LoanOfferDto.builder()
                .statementId(UUID.randomUUID())
                .requestedAmount(BigDecimal.valueOf(20000))
                .term(12)
                .monthlyPayment(BigDecimal.valueOf(1000))
                .totalAmount(BigDecimal.valueOf(24000))
                .rate(BigDecimal.valueOf(0.05))
                .isInsuranceEnabled(false)
                .isSalaryClient(false)
                .build();

        finishRegistrationRequestDto = FinishRegistrationRequestDto.builder()
                .gender(Gender.MALE)
                .maritalStatus(MaritalStatus.SINGLE)
                .dependentAmount(0)
                .passportIssueDate(LocalDate.of(2020, 1, 1))
                .passportIssueBranch("Branch1")
                .employment(new EmploymentDto())
                .accountNumber("1234567890")
                .build();

        client = Client.builder()
                .firstName("John")
                .lastName("Doe")
                .middleName("Smith")
                .birthDate(LocalDate.of(1990, 1, 1))
                .email("john.doe@example.com")
                .gender(Gender.MALE)
                .maritalStatus(MaritalStatus.SINGLE)
                .dependentAmount(0)
                .build();

        statement = Statement.builder()
                .client(client)
                .status(ApplicationStatus.PREAPPROVAL)
                .build();

        credit = Credit.builder()
                .amount(BigDecimal.valueOf(20000))
                .term(12)
                .monthlyPayment(BigDecimal.valueOf(1000))
                .rate(BigDecimal.valueOf(0.05))
                .psk(BigDecimal.valueOf(0.01))
                .insuranceEnabled(false)
                .salaryClient(false)
                .creditStatus(CreditStatus.CALCULATED)
                .build();

        scoringDataDto = ScoringDataDto.builder()
                .amount(BigDecimal.valueOf(20000))
                .term(12)
                .firstName("John")
                .lastName("Doe")
                .middleName("Smith")
                .gender(Gender.MALE)
                .birthdate(LocalDate.of(1990, 1, 1))
                .passportSeries("1234")
                .passportNumber("567890")
                .passportIssueDate(LocalDate.of(2020, 1, 1))
                .passportIssueBranch("Branch1")
                .dependentAmount(0)
                .employment(new EmploymentDto())
                .accountNumber("1234567890")
                .isInsuranceEnabled(false)
                .isSalaryClient(false)
                .build();

        creditDto = CreditDto.builder()
                .amount(BigDecimal.valueOf(20000))
                .term(12)
                .monthlyPayment(BigDecimal.valueOf(1000))
                .psk(BigDecimal.valueOf(24000))
                .rate(BigDecimal.valueOf(0.05))
                .psk(BigDecimal.valueOf(0.01))
                .isInsuranceEnabled(false)
                .isSalaryClient(false)
                .paymentSchedule(Arrays.asList(PaymentScheduleElementDto.builder().build()))
                .build();
    }

    @Test
    public void testCalculateLoanOffers() {
        when(clientRepository.save(any(Client.class))).thenReturn(client);
        when(statementRepository.save(any(Statement.class))).thenReturn(statement);
        when(webClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(anyString())).thenReturn(requestBodySpec);
        when(requestBodySpec.body(any(Mono.class), eq(LoanStatementRequestDto.class))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToFlux(LoanOfferDto.class)).thenReturn(Flux.just(loanOfferDto));

        List<LoanOfferDto> result = dealService.calculateLoanOffers(loanStatementRequestDto);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(loanOfferDto, result.get(0));

        verify(clientRepository, times(1)).save(any(Client.class));
        verify(statementRepository, times(1)).save(any(Statement.class));
        verify(webClient, times(1)).post();
    }

    @Test
    public void testSelectLoanOffer() {
        when(statementRepository.findById(any(UUID.class))).thenReturn(java.util.Optional.of(statement));
        when(statusHistoryRepository.save(any(StatusHistory.class))).thenReturn(StatusHistory.builder().build());
        when(statementRepository.save(any(Statement.class))).thenReturn(statement);

        dealService.selectLoanOffer(loanOfferDto);

        assertEquals(ApplicationStatus.APPROVED, statement.getStatus());
        assertEquals(loanOfferDto.toString(), statement.getAppliedOffer());

        verify(statementRepository, times(1)).findById(any(UUID.class));
        verify(statusHistoryRepository, times(1)).save(any(StatusHistory.class));
        verify(statementRepository, times(1)).save(any(Statement.class));
    }
}
