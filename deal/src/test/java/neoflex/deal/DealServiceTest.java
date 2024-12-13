package neoflex.deal;

import neoflex.deal.dto.LoanOfferDto;
import neoflex.deal.dto.LoanStatementRequestDto;
import neoflex.deal.enums.Gender;
import neoflex.deal.enums.MaritalStatus;
import neoflex.deal.service.DealService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class DealServiceTest {

    @Mock
    private WebClient webClient;

    @Mock
    private WebClient.RequestBodyUriSpec requestBodyUriSpec;

    @Mock
    private WebClient.RequestBodySpec requestBodySpec;

    @Mock
    private WebClient.ResponseSpec responseSpec;

    @InjectMocks
    private DealService dealService;

    private LoanStatementRequestDto loanStatementRequestDto;
    private List<LoanOfferDto> loanOffers;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

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

        loanOffers = Arrays.asList(
                LoanOfferDto.builder()
                        .statementId(UUID.randomUUID())
                        .requestedAmount(BigDecimal.valueOf(20000))
                        .term(12)
                        .monthlyPayment(BigDecimal.valueOf(1000))
                        .totalAmount(BigDecimal.valueOf(24000))
                        .rate(BigDecimal.valueOf(0.05))
                        .isInsuranceEnabled(false)
                        .isSalaryClient(false)
                        .build(),
                LoanOfferDto.builder()
                        .statementId(UUID.randomUUID())
                        .requestedAmount(BigDecimal.valueOf(20000))
                        .term(12)
                        .monthlyPayment(BigDecimal.valueOf(1050))
                        .totalAmount(BigDecimal.valueOf(25200))
                        .rate(BigDecimal.valueOf(0.04))
                        .isInsuranceEnabled(true)
                        .isSalaryClient(false)
                        .build(),
                LoanOfferDto.builder()
                        .statementId(UUID.randomUUID())
                        .requestedAmount(BigDecimal.valueOf(20000))
                        .term(12)
                        .monthlyPayment(BigDecimal.valueOf(1020))
                        .totalAmount(BigDecimal.valueOf(24480))
                        .rate(BigDecimal.valueOf(0.03))
                        .isInsuranceEnabled(false)
                        .isSalaryClient(true)
                        .build(),
                LoanOfferDto.builder()
                        .statementId(UUID.randomUUID())
                        .requestedAmount(BigDecimal.valueOf(20000))
                        .term(12)
                        .monthlyPayment(BigDecimal.valueOf(1065))
                        .totalAmount(BigDecimal.valueOf(25560))
                        .rate(BigDecimal.valueOf(0.02))
                        .isInsuranceEnabled(true)
                        .isSalaryClient(true)
                        .build()
        );

        // Настройка мока для WebClient
        when(webClient.post()).thenReturn(requestBodySpec);
        when(requestBodySpec.uri("/offers")).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.body(any(Mono.class), any(Class.class))).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToFlux(LoanOfferDto.class)).thenReturn(Mono.just(loanOffers));
    }

    @Test
    public void testCalculateLoanOffers() {
        List<LoanOfferDto> result = dealService.calculateLoanOffers(loanStatementRequestDto);

        assertEquals(4, result.size());
        assertEquals(loanOffers, result);
    }
}
