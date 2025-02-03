package neoflex.deal.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import neoflex.dto.*;
import neoflex.enums.EmploymentPosition;
import neoflex.enums.EmploymentStatus;
import neoflex.enums.Gender;
import neoflex.enums.MaritalStatus;
import neoflex.deal.service.DealService;
import neoflex.enums.Theme;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class DealControllerTest {

    @Mock
    private DealService dealService;

    @Mock
    private KafkaTemplate<String, EmailMessage> kafkaTemplate;

    @InjectMocks
    private DealController dealController;

    private MockMvc mockMvc;

    private LoanStatementRequestDto loanStatementRequestDto;
    private LoanOfferDto loanOfferDto;
    private FinishRegistrationRequestDto finishRegistrationRequestDto;
    private EmailMessage emailMessage;
    private EmailMessage emailMessageWithPdf;

    private ObjectMapper objectMapper;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(dealController).build();

        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        loanStatementRequestDto = LoanStatementRequestDto.builder()
                .firstName("John")
                .lastName("Doe")
                .middleName("Smith")
                .birthDate(LocalDate.of(1990, 1, 1))
                .email("john.doe@example.com")
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
                .employment(EmploymentDto.builder()
                        .employmentStatus(EmploymentStatus.EMPLOYED)
                        .employerINN("1234567890")
                        .salary(BigDecimal.valueOf(50000))
                        .position(EmploymentPosition.WORKER)
                        .workExperienceTotal(5)
                        .workExperienceCurrent(3)
                        .build())
                .accountNumber("1234567890")
                .build();

        emailMessage = EmailMessage.builder()
                .statementId(UUID.randomUUID())
                .theme(Theme.FINISH_REGISTRATION)
                .text("Test Message")
                .build();

        byte[] pdfBytes = "Test PDF Content".getBytes();
        emailMessageWithPdf = EmailMessage.builder()
                .statementId(UUID.randomUUID())
                .theme(Theme.SEND_DOCUMENTS)
                .address("test@example.com")
                .pdfDocumentBytes(pdfBytes)
                .build();
    }

    @Test
    public void testCalculateLoanOffers() throws Exception {
        when(dealService.calculateLoanOffers(any(LoanStatementRequestDto.class)))
                .thenReturn(Arrays.asList(loanOfferDto));

        mockMvc.perform(post("/deal/statement")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(loanStatementRequestDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    public void testSelectLoanOffer() throws Exception {
        when(dealService.selectLoanOffer(any(LoanOfferDto.class))).thenReturn(emailMessage);
        when(kafkaTemplate.send(eq("finish-registration"), any(EmailMessage.class)))
                .thenReturn(CompletableFuture.completedFuture(null));

        mockMvc.perform(post("/deal/offer/select")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(loanOfferDto)))
                .andExpect(status().isOk());

        verify(kafkaTemplate, times(1)).send(eq("finish-registration"), any(EmailMessage.class));
    }

    @Test
    public void testFinishRegistration() throws Exception {
        UUID statementId = UUID.randomUUID();
        when(dealService.finishRegistration(eq(statementId.toString()), any(FinishRegistrationRequestDto.class))).thenReturn(emailMessage);
        when(kafkaTemplate.send(eq("create-documents"), any(EmailMessage.class)))
                .thenReturn(CompletableFuture.completedFuture(null));

        mockMvc.perform(post("/deal/calculate/{statementId}", statementId.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(finishRegistrationRequestDto)))
                .andExpect(status().isOk());

        verify(kafkaTemplate, times(1)).send(eq("create-documents"), any(EmailMessage.class));
    }

    @Test
    public void testSendDocuments() throws Exception {
        UUID statementId = UUID.randomUUID();
        when(dealService.sendDocuments(eq(statementId.toString()))).thenReturn(emailMessageWithPdf);

        CompletableFuture<SendResult<String, EmailMessage>> future = new CompletableFuture<>();
        when(kafkaTemplate.send(eq("send-documents"), any(EmailMessage.class))).thenReturn(future);

        mockMvc.perform(post("/deal/document/{statementId}/send", statementId.toString()))
                .andExpect(status().isOk());

        verify(kafkaTemplate, times(1)).send(eq("send-documents"), any(EmailMessage.class));
    }

    @Test
    public void testSignDocuments() throws Exception {
        UUID statementId = UUID.randomUUID();
        String sesCode = "12345678";
        EmailMessage emailMessageWithSesCode = EmailMessage.builder()
                .statementId(statementId)
                .theme(Theme.SEND_SES)
                .address("test@example.com")
                .text("Потвердите согласие на оформление кредита с помощью кода: " + sesCode)
                .build();

        when(dealService.signDocuments(eq(statementId.toString()))).thenReturn(emailMessageWithSesCode);
        when(kafkaTemplate.send(eq("send-ses"), any(EmailMessage.class)))
                .thenReturn(CompletableFuture.completedFuture(null));

        mockMvc.perform(post("/deal/document/{statementId}/sign", statementId.toString()))
                .andExpect(status().isOk());

        verify(kafkaTemplate, times(1)).send(eq("send-ses"), any(EmailMessage.class));
    }

    @Test
    public void testCodeDocumentsSuccess() throws Exception {
        UUID statementId = UUID.randomUUID();
        String sesCode = "12345678";
        EmailMessage emailMessageCreditIssued = EmailMessage.builder()
                .statementId(statementId)
                .theme(Theme.CREDIT_ISSUED)
                .address("test@example.com")
                .build();

        when(dealService.codeDocuments(eq(statementId.toString()), eq(sesCode))).thenReturn(emailMessageCreditIssued);
        when(kafkaTemplate.send(eq("credit-issued"), any(EmailMessage.class)))
                .thenReturn(CompletableFuture.completedFuture(null));

        mockMvc.perform(post("/deal/document/{statementId}/code", statementId.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(sesCode))
                .andExpect(status().isOk());

        verify(kafkaTemplate, times(1)).send(eq("credit-issued"), any(EmailMessage.class));
    }

    @Test
    public void testCodeDocumentsInvalidSesCode() throws Exception {
        UUID statementId = UUID.randomUUID();
        String invalidSesCode = "87654321";

        when(dealService.codeDocuments(eq(statementId.toString()), eq(invalidSesCode)))
                .thenThrow(new IllegalArgumentException("Неверный SES код"));

        mockMvc.perform(post("/deal/document/{statementId}/code", statementId.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidSesCode))
                .andExpect(status().isBadRequest());

        verify(kafkaTemplate, never()).send(anyString(), any(EmailMessage.class));
    }

    private String asJsonString(final Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
