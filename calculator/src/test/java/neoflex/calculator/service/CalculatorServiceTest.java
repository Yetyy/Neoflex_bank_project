package neoflex.calculator.service;

import neoflex.calculator.CalculatorApplication;
import neoflex.dto.*;
import neoflex.enums.EmploymentPosition;
import neoflex.enums.EmploymentStatus;
import neoflex.enums.Gender;
import neoflex.enums.MaritalStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = CalculatorApplication.class)
public class CalculatorServiceTest {

    @Autowired
    private CalculatorService calculatorService;

    private LoanStatementRequestDto request;
    private ScoringDataDto scoringData;

    @BeforeEach
    void setUp() {
        // Инициализация данных для теста generateLoanOffers
        request = new LoanStatementRequestDto();
        request.setAmount(BigDecimal.valueOf(500000));
        request.setTerm(24);
        request.setBirthDate(LocalDate.now().minusYears(30));

        // Инициализация данных для теста calculateCredit
        EmploymentDto employment = new EmploymentDto();
        employment.setEmploymentStatus(EmploymentStatus.EMPLOYED);
        employment.setSalary(BigDecimal.valueOf(600000));
        employment.setPosition(EmploymentPosition.WORKER);
        employment.setWorkExperienceTotal(20);
        employment.setWorkExperienceCurrent(20);

        scoringData = new ScoringDataDto();
        scoringData.setAmount(BigDecimal.valueOf(100000));
        scoringData.setTerm(12);
        scoringData.setGender(Gender.MALE);
        scoringData.setBirthdate(LocalDate.of(2003, 5, 20));
        scoringData.setMaritalStatus(MaritalStatus.SINGLE);
        scoringData.setEmployment(employment);
        scoringData.setIsInsuranceEnabled(false);
        scoringData.setIsSalaryClient(false);
    }

    @Test
    void testGenerateLoanOffers_allCombinations() {
        List<LoanOfferDto> offers = calculatorService.generateLoanOffers(request);
        assertEquals(4, offers.size());

        // Проверка всех комбинаций isInsuranceEnabled и isSalaryClient
        assertTrue(offers.stream().anyMatch(o -> !o.isInsuranceEnabled() && !o.isSalaryClient()));
        assertTrue(offers.stream().anyMatch(o -> !o.isInsuranceEnabled() && o.isSalaryClient()));
        assertTrue(offers.stream().anyMatch(o -> o.isInsuranceEnabled() && !o.isSalaryClient()));
        assertTrue(offers.stream().anyMatch(o -> o.isInsuranceEnabled() && o.isSalaryClient()));
    }

    @Test
    void testGenerateLoanOffers_ageLessThan20() {
        request.setBirthDate(LocalDate.now().minusYears(19));
        assertThrows(IllegalArgumentException.class, () -> calculatorService.generateLoanOffers(request));
    }

    @Test
    void testGenerateLoanOffers_ageMoreThan65() {
        request.setBirthDate(LocalDate.now().minusYears(66));
        assertThrows(IllegalArgumentException.class, () -> calculatorService.generateLoanOffers(request));
    }

    @Test
    void testGenerateLoanOffers_sortOrder() {
        List<LoanOfferDto> offers = calculatorService.generateLoanOffers(request);
        for (int i = 1; i < offers.size(); i++) {
            assertTrue(offers.get(i - 1).getRate().compareTo(offers.get(i).getRate()) <= 0);
        }
    }

    @Test
    void testCalculateCredit_Unemployed() {
        EmploymentDto employment = scoringData.getEmployment();
        employment.setEmploymentStatus(EmploymentStatus.UNEMPLOYED);
        scoringData.setEmployment(employment);
        assertThrows(IllegalArgumentException.class, () -> calculatorService.calculateCredit(scoringData));
    }


    @Test
    void testCalculateCredit_MiddleManager() {
        EmploymentDto employment = scoringData.getEmployment();
        employment.setPosition(EmploymentPosition.MIDDLE_MANAGER);
        scoringData.setEmployment(employment);
        CreditDto creditData = calculatorService.calculateCredit(scoringData);
        assertNotNull(creditData);
        assertTrue(creditData.getRate().compareTo(BigDecimal.valueOf(0.02)) < 0);
    }

    @Test
    void testCalculateCredit_TopManager() {
        EmploymentDto employment = scoringData.getEmployment();
        employment.setPosition(EmploymentPosition.TOP_MANAGER);
        scoringData.setEmployment(employment);
        CreditDto creditData = calculatorService.calculateCredit(scoringData);
        assertNotNull(creditData);
        assertTrue(creditData.getRate().compareTo(BigDecimal.valueOf(0.03)) < 0);
    }

    @Test
    void testCalculateCredit_LoanAmountExceedsSalary() {
        scoringData.setAmount(BigDecimal.valueOf(600000).multiply(BigDecimal.valueOf(25)));
        assertThrows(IllegalArgumentException.class, () -> calculatorService.calculateCredit(scoringData));
    }

    @Test
    void testCalculateCredit_Married() {
        scoringData.setMaritalStatus(MaritalStatus.MARRIED);
        CreditDto creditData = calculatorService.calculateCredit(scoringData);
        assertNotNull(creditData);
        assertTrue(creditData.getRate().compareTo(BigDecimal.valueOf(0.03)) < 0);
    }


    @Test
    void testCalculateCredit_FemaleAge32to60() {
        scoringData.setGender(Gender.FEMALE);
        scoringData.setBirthdate(LocalDate.of(1990, 5, 20)); // 32 years old
        CreditDto creditData = calculatorService.calculateCredit(scoringData);
        assertNotNull(creditData);
        assertTrue(creditData.getRate().compareTo(BigDecimal.valueOf(0.03)) < 0);
    }

    @Test
    void testCalculateCredit_MaleAge30to55() {
        scoringData.setGender(Gender.MALE);
        scoringData.setBirthdate(LocalDate.of(1993, 5, 20)); // 30 years old
        CreditDto creditData = calculatorService.calculateCredit(scoringData);
        assertNotNull(creditData);
        assertTrue(creditData.getRate().compareTo(BigDecimal.valueOf(0.03)) < 0);
    }


    @Test
    void testCalculateCredit_InsufficientWorkExperience() {
        EmploymentDto employment = scoringData.getEmployment();
        employment.setWorkExperienceTotal(17);
        scoringData.setEmployment(employment);
        assertThrows(IllegalArgumentException.class, () -> calculatorService.calculateCredit(scoringData));
    }

    @Test
    void testCalculateCredit_InsufficientCurrentWorkExperience() {
        EmploymentDto employment = scoringData.getEmployment();
        employment.setWorkExperienceCurrent(2);
        scoringData.setEmployment(employment);
        assertThrows(IllegalArgumentException.class, () -> calculatorService.calculateCredit(scoringData));
    }

    @Test
    void testCalculateCredit_BusinessOwner() {
        // 1. Создаем базовый сценарий
        ScoringDataDto baseScoringData = new ScoringDataDto();
        baseScoringData.setAmount(scoringData.getAmount());
        baseScoringData.setTerm(scoringData.getTerm());
        baseScoringData.setGender(scoringData.getGender());
        baseScoringData.setBirthdate(scoringData.getBirthdate());
        baseScoringData.setMaritalStatus(scoringData.getMaritalStatus());
        baseScoringData.setEmployment(scoringData.getEmployment());
        baseScoringData.setIsInsuranceEnabled(scoringData.getIsInsuranceEnabled());
        baseScoringData.setIsSalaryClient(scoringData.getIsSalaryClient());
        CreditDto baseCredit = calculatorService.calculateCredit(baseScoringData);

        // 2. Создаем сценарий с BUSINESS_OWNER
        ScoringDataDto businessOwnerScoringData = new ScoringDataDto();
        businessOwnerScoringData.setAmount(scoringData.getAmount());
        businessOwnerScoringData.setTerm(scoringData.getTerm());
        businessOwnerScoringData.setGender(scoringData.getGender());
        businessOwnerScoringData.setBirthdate(scoringData.getBirthdate());
        businessOwnerScoringData.setMaritalStatus(scoringData.getMaritalStatus());
        EmploymentDto businessOwnerEmployment = new EmploymentDto();
        businessOwnerEmployment.setEmploymentStatus(EmploymentStatus.BUSINESS_OWNER);
        businessOwnerEmployment.setSalary(scoringData.getEmployment().getSalary());
        businessOwnerEmployment.setPosition(scoringData.getEmployment().getPosition());
        businessOwnerEmployment.setWorkExperienceTotal(scoringData.getEmployment().getWorkExperienceTotal());
        businessOwnerEmployment.setWorkExperienceCurrent(scoringData.getEmployment().getWorkExperienceCurrent());
        businessOwnerScoringData.setEmployment(businessOwnerEmployment);
        businessOwnerScoringData.setIsInsuranceEnabled(scoringData.getIsInsuranceEnabled());
        businessOwnerScoringData.setIsSalaryClient(scoringData.getIsSalaryClient());
        CreditDto businessOwnerCredit = calculatorService.calculateCredit(businessOwnerScoringData);

        // 3. Проверяем разницу в ставках
        BigDecimal expectedRateDifference = BigDecimal.valueOf(0.01);
        BigDecimal actualRateDifference = businessOwnerCredit.getRate().subtract(baseCredit.getRate());
        assertEquals(expectedRateDifference, actualRateDifference);
    }

    @Test
    void testCalculateCredit_NonBinary() {
        // 1. Создаем базовый сценарий
        ScoringDataDto baseScoringData = new ScoringDataDto();
        baseScoringData.setAmount(scoringData.getAmount());
        baseScoringData.setTerm(scoringData.getTerm());
        baseScoringData.setGender(scoringData.getGender());
        baseScoringData.setBirthdate(scoringData.getBirthdate());
        baseScoringData.setMaritalStatus(scoringData.getMaritalStatus());
        baseScoringData.setEmployment(scoringData.getEmployment());
        baseScoringData.setIsInsuranceEnabled(scoringData.getIsInsuranceEnabled());
        baseScoringData.setIsSalaryClient(scoringData.getIsSalaryClient());
        CreditDto baseCredit = calculatorService.calculateCredit(baseScoringData);

        // 2. Создаем сценарий с NON_BINARY
        ScoringDataDto nonBinaryScoringData = new ScoringDataDto();
        nonBinaryScoringData.setAmount(scoringData.getAmount());
        nonBinaryScoringData.setTerm(scoringData.getTerm());
        nonBinaryScoringData.setGender(Gender.NON_BINARY);
        nonBinaryScoringData.setBirthdate(scoringData.getBirthdate());
        nonBinaryScoringData.setMaritalStatus(scoringData.getMaritalStatus());
        nonBinaryScoringData.setEmployment(scoringData.getEmployment());
        nonBinaryScoringData.setIsInsuranceEnabled(scoringData.getIsInsuranceEnabled());
        nonBinaryScoringData.setIsSalaryClient(scoringData.getIsSalaryClient());
        CreditDto nonBinaryCredit = calculatorService.calculateCredit(nonBinaryScoringData);

        // 3. Проверяем разницу в ставках
        BigDecimal expectedRateDifference = BigDecimal.valueOf(0.07);
        BigDecimal actualRateDifference = nonBinaryCredit.getRate().subtract(baseCredit.getRate());
        assertEquals(expectedRateDifference, actualRateDifference);
    }

    @Test
    void testCalculateCredit_Divorced() {
        // 1. Создаем базовый сценарий
        ScoringDataDto baseScoringData = new ScoringDataDto();
        baseScoringData.setAmount(scoringData.getAmount());
        baseScoringData.setTerm(scoringData.getTerm());
        baseScoringData.setGender(scoringData.getGender());
        baseScoringData.setBirthdate(scoringData.getBirthdate());
        baseScoringData.setMaritalStatus(scoringData.getMaritalStatus());
        baseScoringData.setEmployment(scoringData.getEmployment());
        baseScoringData.setIsInsuranceEnabled(scoringData.getIsInsuranceEnabled());
        baseScoringData.setIsSalaryClient(scoringData.getIsSalaryClient());
        CreditDto baseCredit = calculatorService.calculateCredit(baseScoringData);

        // 2. Создаем сценарий с DIVORCED
        ScoringDataDto divorcedScoringData = new ScoringDataDto();
        divorcedScoringData.setAmount(scoringData.getAmount());
        divorcedScoringData.setTerm(scoringData.getTerm());
        divorcedScoringData.setGender(scoringData.getGender());
        divorcedScoringData.setBirthdate(scoringData.getBirthdate());
        divorcedScoringData.setMaritalStatus(MaritalStatus.DIVORCED);
        divorcedScoringData.setEmployment(scoringData.getEmployment());
        divorcedScoringData.setIsInsuranceEnabled(scoringData.getIsInsuranceEnabled());
        divorcedScoringData.setIsSalaryClient(scoringData.getIsSalaryClient());
        CreditDto divorcedCredit = calculatorService.calculateCredit(divorcedScoringData);

        // 3. Проверяем разницу в ставках
        BigDecimal expectedRateDifference = BigDecimal.valueOf(0.01);
        BigDecimal actualRateDifference = divorcedCredit.getRate().subtract(baseCredit.getRate());
        assertEquals(expectedRateDifference, actualRateDifference);
    }

    @Test
    void testCalculateCredit_WidowWidower() {
        // 1. Создаем базовый сценарий
        ScoringDataDto baseScoringData = new ScoringDataDto();
        baseScoringData.setAmount(scoringData.getAmount());
        baseScoringData.setTerm(scoringData.getTerm());
        baseScoringData.setGender(scoringData.getGender());
        baseScoringData.setBirthdate(scoringData.getBirthdate());
        baseScoringData.setMaritalStatus(scoringData.getMaritalStatus());
        baseScoringData.setEmployment(scoringData.getEmployment());
        baseScoringData.setIsInsuranceEnabled(scoringData.getIsInsuranceEnabled());
        baseScoringData.setIsSalaryClient(scoringData.getIsSalaryClient());
        CreditDto baseCredit = calculatorService.calculateCredit(baseScoringData);

        // 2. Создаем сценарий с WIDOW_WIDOWER
        ScoringDataDto widowWidowerScoringData = new ScoringDataDto();
        widowWidowerScoringData.setAmount(scoringData.getAmount());
        widowWidowerScoringData.setTerm(scoringData.getTerm());
        widowWidowerScoringData.setGender(scoringData.getGender());
        widowWidowerScoringData.setBirthdate(scoringData.getBirthdate());
        widowWidowerScoringData.setMaritalStatus(MaritalStatus.WIDOW_WIDOWER);
        widowWidowerScoringData.setEmployment(scoringData.getEmployment());
        widowWidowerScoringData.setIsInsuranceEnabled(scoringData.getIsInsuranceEnabled());
        widowWidowerScoringData.setIsSalaryClient(scoringData.getIsSalaryClient());
        CreditDto widowWidowerCredit = calculatorService.calculateCredit(widowWidowerScoringData);

        // 3. Проверяем разницу в ставках
        BigDecimal expectedRateDifference = BigDecimal.valueOf(0.02);
        BigDecimal actualRateDifference = widowWidowerCredit.getRate().subtract(baseCredit.getRate());
        assertEquals(expectedRateDifference, actualRateDifference);
    }


    @Test
    void testCalculateCredit_allFieldsFilled() {
        CreditDto creditData = calculatorService.calculateCredit(scoringData);
        assertNotNull(creditData.getAmount());
        assertNotNull(creditData.getTerm());
        assertNotNull(creditData.getRate());
        assertNotNull(creditData.getMonthlyPayment());
        assertNotNull(creditData.getPsk());
        assertNotNull(creditData.getIsInsuranceEnabled());
        assertNotNull(creditData.getIsSalaryClient());
        assertNotNull(creditData.getPaymentSchedule());
    }
}
