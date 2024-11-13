package neoflex.calculator.service;

import lombok.Getter;
import neoflex.calculator.dto.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

import static neoflex.calculator.util.AgeUtils.calculateAge;

@Service
public class LoanOfferService {
    @Getter
    @Value("${base.interest.rate}")
    private BigDecimal baseInterestRate;

    private static final BigDecimal INSURANCE_DISCOUNT = BigDecimal.valueOf(0.03);
    private static final BigDecimal SALARY_CLIENT_DISCOUNT = BigDecimal.valueOf(0.01);

    public List<LoanOfferDto> generateLoanOffers(LoanStatementRequestDto request) {
        List<LoanOfferDto> offers = new ArrayList<>();

        offers.add(createLoanOffer(request, false, false));
        offers.add(createLoanOffer(request, false, true));
        offers.add(createLoanOffer(request, true, false));
        offers.add(createLoanOffer(request, true, true));

        offers.sort(Comparator.comparing(LoanOfferDto::getRate));
        return offers;
    }

    private LoanOfferDto createLoanOffer(LoanStatementRequestDto request, boolean isInsuranceEnabled, boolean isSalaryClient) {
        BigDecimal interestRate = baseInterestRate;

        if (isInsuranceEnabled) {
            interestRate = interestRate.subtract(INSURANCE_DISCOUNT);
        }
        if (isSalaryClient) {
            interestRate = interestRate.subtract(SALARY_CLIENT_DISCOUNT);
        }

        BigDecimal loanAmount = request.getAmount();

        BigDecimal annuityPayment = calculateAnnuityMonthlyPayment(loanAmount, interestRate, request.getTerm());
        BigDecimal differentiatedPayment = calculateDifferentiatedMonthlyPayment(loanAmount, interestRate, request.getTerm());

        BigDecimal annuityTotalAmount = annuityPayment.multiply(BigDecimal.valueOf(request.getTerm()));
        BigDecimal differentiatedTotalAmount = calculateDifferentiatedTotalAmount(loanAmount, interestRate, request.getTerm());

        if (isInsuranceEnabled) {
            BigDecimal insuranceCost = loanAmount.multiply(BigDecimal.valueOf(0.01));
            annuityTotalAmount = annuityTotalAmount.add(insuranceCost);
            differentiatedTotalAmount = differentiatedTotalAmount.add(insuranceCost);
        }

        return new LoanOfferDto(
                UUID.randomUUID(),
                request.getAmount(),
                request.getTerm(),
                annuityPayment,
                differentiatedPayment,
                annuityTotalAmount,
                differentiatedTotalAmount,
                interestRate,
                isInsuranceEnabled,
                isSalaryClient
        );
    }

    BigDecimal calculateAnnuityMonthlyPayment(BigDecimal loanAmount, BigDecimal Rate, int termMonths) {
        BigDecimal monthlyRate = Rate.divide(BigDecimal.valueOf(12), MathContext.DECIMAL128);
        BigDecimal onePlusRateToPowerTerm = BigDecimal.ONE.add(monthlyRate).pow(termMonths, MathContext.DECIMAL128);
        BigDecimal numerator = loanAmount.multiply(monthlyRate).multiply(onePlusRateToPowerTerm);
        BigDecimal denominator = onePlusRateToPowerTerm.subtract(BigDecimal.ONE);
        return numerator.divide(denominator, 2, RoundingMode.HALF_UP);
    }

    BigDecimal calculateDifferentiatedMonthlyPayment(BigDecimal loanAmount, BigDecimal Rate, int termMonths) {
        BigDecimal monthlyRate = Rate.divide(BigDecimal.valueOf(12), MathContext.DECIMAL128);
        BigDecimal principalPayment = loanAmount.divide(BigDecimal.valueOf(termMonths), MathContext.DECIMAL128);
        BigDecimal interestPayment = loanAmount.multiply(monthlyRate);
        return principalPayment.add(interestPayment).setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal calculateDifferentiatedTotalAmount(BigDecimal loanAmount, BigDecimal Rate, int termMonths) {
        BigDecimal totalAmount = BigDecimal.ZERO;
        BigDecimal monthlyRate = Rate.divide(BigDecimal.valueOf(12), MathContext.DECIMAL128);
        BigDecimal principalPayment = loanAmount.divide(BigDecimal.valueOf(termMonths), MathContext.DECIMAL128);
        BigDecimal remainingPrincipal = loanAmount;

        for (int i = 0; i < termMonths; i++) {
            BigDecimal interestPayment = remainingPrincipal.multiply(monthlyRate);
            remainingPrincipal = remainingPrincipal.subtract(principalPayment);
            totalAmount = totalAmount.add(principalPayment.add(interestPayment));
        }

        return totalAmount;
    }

    public CreditDto calculateCredit(ScoringDataDto scoringData) {
        BigDecimal rate = baseInterestRate;

        // Применение правил скоринга по статусу занятости
        switch (scoringData.getEmployment().getEmploymentStatus()) {
            case UNEMPLOYED:
                throw new IllegalArgumentException("Отказ: статус - безработный.");
            case SELF_EMPLOYED:
                rate = rate.add(BigDecimal.valueOf(2));
                break;
            case BUSINESS_OWNER:
                rate = rate.add(BigDecimal.valueOf(1));
                break;
            default:
                break;
        }

        // Применение правил скоринга по позиции на работе
        switch (scoringData.getEmployment().getPosition()) {
            case MIDDLE_MANAGER:
                rate = rate.subtract(BigDecimal.valueOf(2));
                break;
            case TOP_MANAGER:
                rate = rate.subtract(BigDecimal.valueOf(3));
                break;
            default:
                break;
        }

        // Применение правил скоринга по сумме займа
        if (scoringData.getAmount().compareTo(scoringData.getEmployment().getSalary().multiply(BigDecimal.valueOf(24))) > 0) {
            throw new IllegalArgumentException("Отказ: сумма займа больше, чем 24 зарплат.");
        }

        // Применение правил скоринга по семейному положению
        switch (scoringData.getMaritalStatus()) {
            case MARRIED:
                rate = rate.subtract(BigDecimal.valueOf(3));
                break;
            case DIVORCED:
                rate = rate.add(BigDecimal.valueOf(1));
                break;
            default:
                break;
        }

        // Применение правил скоринга по полу
        int age = calculateAge(scoringData.getBirthdate(), LocalDate.now());
        if (scoringData.getGender() == Gender.FEMALE && age >= 32 && age <= 60) {
            rate = rate.subtract(BigDecimal.valueOf(3));
        } else if (scoringData.getGender() == Gender.MALE && age >= 30 && age <= 55) {
            rate = rate.subtract(BigDecimal.valueOf(3));
        } else if (scoringData.getGender() == Gender.NON_BINARY) {
            rate = rate.add(BigDecimal.valueOf(7));
        }

        // Применение правил скоринга по стажу работы
        if (scoringData.getEmployment().getWorkExperienceTotal() < 18 || scoringData.getEmployment().getWorkExperienceCurrent() < 3) {
            throw new IllegalArgumentException("Отказ: стаж работы менее 18 месяцев или текущий стаж менее 3 месяцев.");
        }

        // Расчет аннуитентного платежа и ПСК
        BigDecimal annuityMonthlyPayment = calculateAnnuityMonthlyPayment(scoringData.getAmount(), rate, scoringData.getTerm());
        BigDecimal annuityPsk = annuityMonthlyPayment.multiply(BigDecimal.valueOf(scoringData.getTerm()));
        List<PaymentScheduleElementDto> annuityPaymentSchedule = calculateAnnuityPaymentSchedule(scoringData.getAmount(), rate, scoringData.getTerm());

        // Расчет дифференцированного платежа и ПСК
        BigDecimal differentiatedMonthlyPayment = calculateDifferentiatedMonthlyPayment(scoringData.getAmount(), rate, scoringData.getTerm());
        BigDecimal differentiatedPsk = calculateDifferentiatedTotalAmount(scoringData.getAmount(), rate, scoringData.getTerm());
        List<PaymentScheduleElementDto> differentiatedPaymentSchedule = calculateDifferentiatedPaymentSchedule(scoringData.getAmount(), rate, scoringData.getTerm());

        // Создание и возвращение CreditDto
        CreditDto creditDto = new CreditDto();
        creditDto.setAmount(scoringData.getAmount());
        creditDto.setTerm(scoringData.getTerm());
        creditDto.setRate(rate);
        creditDto.setAnnuityMonthlyPayment(annuityMonthlyPayment);
        creditDto.setAnnuityPsk(annuityPsk);
        creditDto.setDifferentiatedMonthlyPayment(differentiatedMonthlyPayment);
        creditDto.setDifferentiatedPsk(differentiatedPsk);
        creditDto.setAnnuityPaymentSchedule(annuityPaymentSchedule);
        creditDto.setDifferentiatedPaymentSchedule(differentiatedPaymentSchedule);

        return creditDto;
    }

    private List<PaymentScheduleElementDto> calculateAnnuityPaymentSchedule(BigDecimal loanAmount, BigDecimal rate, int term) {
        List<PaymentScheduleElementDto> paymentSchedule = new ArrayList<>();
        BigDecimal monthlyRate = rate.divide(BigDecimal.valueOf(12), MathContext.DECIMAL128);
        BigDecimal remainingPrincipal = loanAmount;
        BigDecimal monthlyPayment = calculateAnnuityMonthlyPayment(loanAmount, rate, term);

        for (int i = 0; i < term; i++) {
            BigDecimal interestPayment = remainingPrincipal.multiply(monthlyRate);
            BigDecimal principalPayment = monthlyPayment.subtract(interestPayment);
            remainingPrincipal = remainingPrincipal.subtract(principalPayment);

            PaymentScheduleElementDto element = new PaymentScheduleElementDto();
            element.setNumber(i + 1);
            element.setDate(LocalDate.now().plusMonths(i));
            element.setTotalPayment(monthlyPayment);
            element.setInterestPayment(interestPayment);
            element.setDebtPayment(principalPayment);
            element.setRemainingDebt(remainingPrincipal);

            paymentSchedule.add(element);
        }

        return paymentSchedule;
    }

    private List<PaymentScheduleElementDto> calculateDifferentiatedPaymentSchedule(BigDecimal loanAmount, BigDecimal rate, int term) {
        List<PaymentScheduleElementDto> paymentSchedule = new ArrayList<>();
        BigDecimal monthlyRate = rate.divide(BigDecimal.valueOf(12), MathContext.DECIMAL128);
        BigDecimal principalPayment = loanAmount.divide(BigDecimal.valueOf(term), MathContext.DECIMAL128);
        BigDecimal remainingPrincipal = loanAmount;

        for (int i = 0; i < term; i++) {
            BigDecimal interestPayment = remainingPrincipal.multiply(monthlyRate);
            remainingPrincipal = remainingPrincipal.subtract(principalPayment);

            PaymentScheduleElementDto element = new PaymentScheduleElementDto();
            element.setNumber(i + 1);
            element.setDate(LocalDate.now().plusMonths(i));
            element.setTotalPayment(principalPayment.add(interestPayment));
            element.setInterestPayment(interestPayment);
            element.setDebtPayment(principalPayment);
            element.setRemainingDebt(remainingPrincipal);

            paymentSchedule.add(element);
        }

        return paymentSchedule;
    }
}
