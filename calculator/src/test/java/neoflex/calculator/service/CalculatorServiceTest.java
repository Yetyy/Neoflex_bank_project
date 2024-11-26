package neoflex.calculator.service;

import neoflex.calculator.dto.*;
import neoflex.calculator.dto.enums.EmploymentStatus;
import neoflex.calculator.dto.enums.Gender;
import neoflex.calculator.dto.enums.MaritalStatus;
import neoflex.calculator.dto.enums.Position;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class CalculatorServiceTest {

    @Autowired
    private CalculatorService loanOfferService;

    private LoanStatementRequestDto request;
    private ScoringDataDto scoringData;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Инициализация данных для теста generateLoanOffers
        request = new LoanStatementRequestDto();
        request.setAmount(BigDecimal.valueOf(500000)); // сумма кредита
        request.setTerm(24); // срок кредита в месяцах

        // Инициализация данных для теста calculateCredit
        scoringData = new ScoringDataDto();
        scoringData.setAmount(BigDecimal.valueOf(100000));
        scoringData.setTerm(12);
        scoringData.setGender(Gender.MALE);
        scoringData.setBirthdate(LocalDate.of(2003, 5, 20));
        scoringData.setMaritalStatus(MaritalStatus.SINGLE);

        EmploymentDto employment = new EmploymentDto();
        employment.setEmploymentStatus(EmploymentStatus.EMPLOYED);
        employment.setSalary(BigDecimal.valueOf(600000));
        employment.setPosition(Position.WORKER);
        employment.setWorkExperienceTotal(20);
        employment.setWorkExperienceCurrent(20);

        scoringData.setEmployment(employment);
        scoringData.setIsInsuranceEnabled(false);
        scoringData.setIsSalaryClient(false);
    }

    @Test
    void testGenerateLoanOffers_noInsuranceNoSalaryClient() {
        LoanStatementRequestDto request = new LoanStatementRequestDto();
        request.setAmount(BigDecimal.valueOf(100000));
        request.setTerm(12);
        request.setBirthDate(LocalDate.now().minusYears(30));

        List<LoanOfferDto> offers = loanOfferService.generateLoanOffers(request);

        assertEquals(4, offers.size());

        LoanOfferDto offer = offers.get(3);
        assertEquals(false, offer.isInsuranceEnabled());
        assertEquals(false, offer.isSalaryClient());
    }


    @Test
    void testGenerateLoanOffers_noInsuranceWithSalaryClient() {
        LoanStatementRequestDto request = new LoanStatementRequestDto();
        request.setAmount(BigDecimal.valueOf(100000));
        request.setTerm(12);
        request.setBirthDate(LocalDate.now().minusYears(30));

        List<LoanOfferDto> offers = loanOfferService.generateLoanOffers(request);

        BigDecimal expectedRate = loanOfferService.getBaseInterestRate().subtract(BigDecimal.valueOf(0.01));

        LoanOfferDto offer = offers.stream()
                .filter(o -> !o.isInsuranceEnabled() && o.isSalaryClient())
                .findFirst()
                .orElseThrow();

        assertEquals(expectedRate, offer.getRate());
        assertEquals(false, offer.isInsuranceEnabled());
        assertEquals(true, offer.isSalaryClient());
    }

    @Test
    void testGenerateLoanOffers_withInsuranceNoSalaryClient() {
        LoanStatementRequestDto request = new LoanStatementRequestDto();
        request.setAmount(BigDecimal.valueOf(100000));
        request.setTerm(12);
        request.setBirthDate(LocalDate.now().minusYears(30));

        List<LoanOfferDto> offers = loanOfferService.generateLoanOffers(request);

        // Проверка ставки с учетом страховки
        BigDecimal expectedRate = loanOfferService.getBaseInterestRate().subtract(BigDecimal.valueOf(0.03));

        LoanOfferDto offer = offers.stream()
                .filter(o -> o.isInsuranceEnabled() && !o.isSalaryClient())
                .findFirst()
                .orElseThrow();

        assertEquals(expectedRate, offer.getRate());
        assertEquals(true, offer.isInsuranceEnabled());
        assertEquals(false, offer.isSalaryClient());
    }

    @Test
    void testGenerateLoanOffers_withInsuranceAndSalaryClient() {
        LoanStatementRequestDto request = new LoanStatementRequestDto();
        request.setAmount(BigDecimal.valueOf(100000));
        request.setTerm(12);
        request.setBirthDate(LocalDate.now().minusYears(30));

        List<LoanOfferDto> offers = loanOfferService.generateLoanOffers(request);

        // Проверка ставки с учетом страховки и зарплатного клиента
        BigDecimal expectedRate = loanOfferService.getBaseInterestRate().subtract(BigDecimal.valueOf(0.04));

        LoanOfferDto offer = offers.stream()
                .filter(o -> o.isInsuranceEnabled() && o.isSalaryClient())
                .findFirst()
                .orElseThrow();

        assertEquals(expectedRate, offer.getRate());
        assertEquals(true, offer.isInsuranceEnabled());
        assertEquals(true, offer.isSalaryClient());
    }

    @Test
    void testGenerateLoanOffers_sortOrder() {
        LoanStatementRequestDto request = new LoanStatementRequestDto();
        request.setAmount(BigDecimal.valueOf(100000));
        request.setTerm(12);
        request.setBirthDate(LocalDate.now().minusYears(30));

        List<LoanOfferDto> offers = loanOfferService.generateLoanOffers(request);

        // Проверка сортировки по ставке (от худшего к лучшему предложению)
        for (int i = 1; i < offers.size(); i++) {
            assertTrue(offers.get(i - 1).getRate().compareTo(offers.get(i).getRate()) <= 0);
        }
    }

    @Test
    public void testGenerateLoanOffers() {
        LoanStatementRequestDto request = new LoanStatementRequestDto();
        request.setAmount(BigDecimal.valueOf(100000));
        request.setTerm(12);
        request.setBirthDate(LocalDate.now().minusYears(30));

        List<LoanOfferDto> loanOffers = loanOfferService.generateLoanOffers(request);

        assertEquals(4, loanOffers.size());
    }

    @Test
    void testGenerateCreditDTO() {
        CreditDto creditData = loanOfferService.calculateCredit(scoringData);

        // Проверка, что результат не null
        assertNotNull(creditData);

    }

    @Test
    void testCalculateCredit_Unemployed() {
        scoringData.getEmployment().setEmploymentStatus(EmploymentStatus.UNEMPLOYED);
        assertThrows(IllegalArgumentException.class, () -> loanOfferService.calculateCredit(scoringData));
    }

    @Test
    void testCalculateCredit_SelfEmployed() {
        scoringData.getEmployment().setEmploymentStatus(EmploymentStatus.SELF_EMPLOYED);
        CreditDto creditData = loanOfferService.calculateCredit(scoringData);
        assertEquals(loanOfferService.getBaseInterestRate().add(BigDecimal.valueOf(0.02)), creditData.getRate());
    }

    @Test
    void testCalculateCredit_BusinessOwner() {
        scoringData.getEmployment().setEmploymentStatus(EmploymentStatus.BUSINESS_OWNER);
        CreditDto creditData = loanOfferService.calculateCredit(scoringData);
        assertEquals(loanOfferService.getBaseInterestRate().add(BigDecimal.valueOf(0.01)), creditData.getRate());
    }

    @Test
    void testCalculateCredit_MiddleManager() {
        scoringData.getEmployment().setPosition(Position.MIDDLE_MANAGER);
        CreditDto creditData = loanOfferService.calculateCredit(scoringData);
        assertEquals(loanOfferService.getBaseInterestRate().subtract(BigDecimal.valueOf(0.02)), creditData.getRate());
    }

    @Test
    void testCalculateCredit_TopManager() {
        scoringData.getEmployment().setPosition(Position.TOP_MANAGER);
        CreditDto creditData = loanOfferService.calculateCredit(scoringData);
        assertEquals(loanOfferService.getBaseInterestRate().subtract(BigDecimal.valueOf(0.03)), creditData.getRate());
    }

    @Test
    void testCalculateCredit_LoanAmountExceedsSalary() {
        scoringData.setAmount(BigDecimal.valueOf(600000).multiply(BigDecimal.valueOf(25)));
        assertThrows(IllegalArgumentException.class, () -> loanOfferService.calculateCredit(scoringData));
    }

    @Test
    void testCalculateCredit_Married() {
        scoringData.setMaritalStatus(MaritalStatus.MARRIED);
        CreditDto creditData = loanOfferService.calculateCredit(scoringData);
        assertEquals(loanOfferService.getBaseInterestRate().subtract(BigDecimal.valueOf(0.03)), creditData.getRate());
    }

    @Test
    void testCalculateCredit_Divorced() {
        scoringData.setMaritalStatus(MaritalStatus.DIVORCED);
        CreditDto creditData = loanOfferService.calculateCredit(scoringData);
        assertEquals(loanOfferService.getBaseInterestRate().add(BigDecimal.valueOf(0.01)), creditData.getRate());
    }

    @Test
    void testCalculateCredit_FemaleAge32to60() {
        scoringData.setGender(Gender.FEMALE);
        scoringData.setBirthdate(LocalDate.of(1990, 5, 20)); // 32 years old
        CreditDto creditData = loanOfferService.calculateCredit(scoringData);
        assertEquals(loanOfferService.getBaseInterestRate().subtract(BigDecimal.valueOf(0.03)), creditData.getRate());
    }

    @Test
    void testCalculateCredit_MaleAge30to55() {
        scoringData.setGender(Gender.MALE);
        scoringData.setBirthdate(LocalDate.of(1993, 5, 20)); // 30 years old
        CreditDto creditData = loanOfferService.calculateCredit(scoringData);
        assertEquals(loanOfferService.getBaseInterestRate().subtract(BigDecimal.valueOf(0.03)), creditData.getRate());
    }

    @Test
    void testCalculateCredit_NonBinary() {
        scoringData.setGender(Gender.NON_BINARY);
        CreditDto creditData = loanOfferService.calculateCredit(scoringData);
        assertEquals(loanOfferService.getBaseInterestRate().add(BigDecimal.valueOf(0.07)), creditData.getRate());
    }

    @Test
    void testCalculateCredit_InsufficientWorkExperience() {
        scoringData.getEmployment().setWorkExperienceTotal(17);
        assertThrows(IllegalArgumentException.class, () -> loanOfferService.calculateCredit(scoringData));
    }

    @Test
    void testCalculateCredit_InsufficientCurrentWorkExperience() {
        scoringData.getEmployment().setWorkExperienceCurrent(2);
        assertThrows(IllegalArgumentException.class, () -> loanOfferService.calculateCredit(scoringData));
    }
}
