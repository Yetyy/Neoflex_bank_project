package neoflex.statement.service;

import neoflex.dto.LoanOfferDto;
import neoflex.dto.LoanStatementRequestDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClient.RequestBodyUriSpec;
import org.springframework.web.reactive.function.client.WebClient.RequestBodySpec;
import org.springframework.web.reactive.function.client.WebClient.RequestHeadersSpec;
import org.springframework.web.reactive.function.client.WebClient.ResponseSpec;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class StatementServiceTest {

    @Mock
    private WebClient webClient;

    @Mock
    private RequestBodyUriSpec requestBodyUriSpec;

    @Mock
    private RequestBodySpec requestBodySpec;

    @Mock
    private RequestHeadersSpec requestHeadersSpec;

    @Mock
    private ResponseSpec responseSpec;

    @InjectMocks
    private StatementService statementService;

    private LoanStatementRequestDto requestDto;
    private LoanOfferDto offerDto1;
    private LoanOfferDto offerDto2;

    @BeforeEach
    void setUp() {
        requestDto = LoanStatementRequestDto.builder()
                .firstName("John")
                .lastName("Doe")
                .middleName("Smith")
                .birthDate(LocalDate.of(1990, 1, 1))
                .email("john.doe@example.com")
                .amount(BigDecimal.valueOf(20000))
                .term(6)
                .passportSeries("1234")
                .passportNumber("567890")
                .build();

        offerDto1 = new LoanOfferDto();
        offerDto2 = new LoanOfferDto();
    }

    @Test
    void getLoanOffersSuccess() {
        List<LoanOfferDto> expectedOffers = Arrays.asList(offerDto1, offerDto2);

        when(webClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri("/deal/statement")).thenReturn(requestBodySpec);
        when(requestBodySpec.body(any(Mono.class), eq(LoanStatementRequestDto.class))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToFlux(LoanOfferDto.class)).thenReturn(Flux.fromIterable(expectedOffers));

        List<LoanOfferDto> actualOffers = statementService.getLoanOffers(requestDto);

        assertEquals(expectedOffers, actualOffers);
    }

    @Test
    void getLoanOffersError() {
        when(webClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri("/deal/statement")).thenReturn(requestBodySpec);
        when(requestBodySpec.body(any(Mono.class), eq(LoanStatementRequestDto.class))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToFlux(LoanOfferDto.class)).thenReturn(Flux.error(new RuntimeException("Test Exception")));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> statementService.getLoanOffers(requestDto));
        assertEquals("Ошибка при получении предложений по кредиту", exception.getMessage());
        assertTrue(exception.getCause() instanceof RuntimeException);
        assertEquals("Test Exception", exception.getCause().getMessage());
    }

    @Test
    void selectLoanOfferSuccess() {
        when(webClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri("/deal/offer/select")).thenReturn(requestBodySpec);
        when(requestBodySpec.body(any(Mono.class), eq(LoanOfferDto.class))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(Void.class)).thenReturn(Mono.empty());

        assertDoesNotThrow(() -> statementService.selectLoanOffer(offerDto1));
    }

    @Test
    void selectLoanOfferError() {
        when(webClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri("/deal/offer/select")).thenReturn(requestBodySpec);
        when(requestBodySpec.body(any(Mono.class), eq(LoanOfferDto.class))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(Void.class)).thenReturn(Mono.error(new WebClientResponseException(400, "Bad Request", null, null, null)));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> statementService.selectLoanOffer(offerDto1));
        assertEquals("Ошибка при выборе кредитного предложения", exception.getMessage());
        assertTrue(exception.getCause() instanceof WebClientResponseException);
        assertEquals(HttpStatus.BAD_REQUEST, ((WebClientResponseException) exception.getCause()).getStatusCode());
    }
}
