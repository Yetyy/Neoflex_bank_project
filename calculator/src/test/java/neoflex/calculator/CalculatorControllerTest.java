package neoflex.calculator.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import neoflex.calculator.CalculatorApplication;
import neoflex.dto.EmploymentDto;
import neoflex.dto.LoanStatementRequestDto;
import neoflex.dto.ScoringDataDto;
import neoflex.enums.EmploymentPosition;
import neoflex.enums.EmploymentStatus;
import neoflex.enums.Gender;
import neoflex.enums.MaritalStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.math.BigDecimal;
import java.time.LocalDate;

@SpringBootTest(classes = CalculatorApplication.class)
@AutoConfigureMockMvc
public class CalculatorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testCalculateLoanOffers() throws Exception {
        LoanStatementRequestDto request = new LoanStatementRequestDto();
        request.setAmount(BigDecimal.valueOf(100000));
        request.setTerm(12);
        request.setBirthDate(LocalDate.now().minusYears(30));

        mockMvc.perform(MockMvcRequestBuilders.post("/calculator/offers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(4));
    }

    @Test
    void testCalculateCreditDetails() throws Exception {
        ScoringDataDto scoringData = ScoringDataDto.builder()
                .amount(BigDecimal.valueOf(100000))
                .term(12)
                .firstName("John")
                .lastName("Doe")
                .middleName("Middle")
                .gender(Gender.MALE)
                .birthdate(LocalDate.of(2003, 5, 20))
                .passportSeries("1234")
                .passportNumber("567890")
                .passportIssueDate(LocalDate.of(2020, 1, 1))
                .passportIssueBranch("Some Branch")
                .maritalStatus(MaritalStatus.SINGLE)
                .dependentAmount(0)
                .employment(EmploymentDto.builder()
                        .employmentStatus(EmploymentStatus.EMPLOYED)
                        .employerINN("1234567890")
                        .salary(BigDecimal.valueOf(600000))
                        .position(EmploymentPosition.WORKER)
                        .workExperienceTotal(20)
                        .workExperienceCurrent(20)
                        .build())
                .accountNumber("12345678901234567890")
                .isInsuranceEnabled(true)
                .isSalaryClient(true)
                .build();


        mockMvc.perform(MockMvcRequestBuilders.post("/calculator/calc")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(scoringData)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.rate").exists());
    }
}
