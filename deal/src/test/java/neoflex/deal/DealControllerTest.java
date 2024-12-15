package neoflex.deal;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import neoflex.deal.controller.DealController;
import neoflex.dto.*;
import neoflex.enums.EmploymentPosition;
import neoflex.enums.EmploymentStatus;
import neoflex.enums.Gender;
import neoflex.enums.MaritalStatus;
import neoflex.deal.service.DealService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class DealControllerTest {

    @Mock
    private DealService dealService;

    @InjectMocks
    private DealController dealController;

    private MockMvc mockMvc;

    private LoanStatementRequestDto loanStatementRequestDto;
    private LoanOfferDto loanOfferDto;
    private FinishRegistrationRequestDto finishRegistrationRequestDto;

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
        mockMvc.perform(post("/deal/offer/select")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(loanOfferDto)))
                .andExpect(status().isOk());
    }

    @Test
    public void testFinishRegistration() throws Exception {
        mockMvc.perform(post("/deal/calculate/{statementId}", UUID.randomUUID().toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(finishRegistrationRequestDto)))
                .andExpect(status().isOk());
    }

    private String asJsonString(final Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
