package neoflex.calculator.service;

import neoflex.calculator.dto.LoanStatementRequestDto;
import neoflex.calculator.dto.LoanOfferDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
public class LoanOfferService {

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
        if (isInsuranceEnabled) {
            BigDecimal insuranceCost = loanAmount.multiply(BigDecimal.valueOf(0.01));
            loanAmount = loanAmount.add(insuranceCost);
        }

        BigDecimal annuityPayment = calculateAnnuityMonthlyPayment(loanAmount, interestRate, request.getTerm());
        BigDecimal differentiatedPayment = calculateDifferentiatedMonthlyPayment(loanAmount, interestRate, request.getTerm());

        return new LoanOfferDto(
                UUID.randomUUID(),
                request.getAmount(),
                loanAmount,
                request.getTerm(),
                annuityPayment,
                differentiatedPayment,
                interestRate,
                isInsuranceEnabled,
                isSalaryClient
        );
    }

    private BigDecimal calculateAnnuityMonthlyPayment(BigDecimal loanAmount, BigDecimal annualRate, int termMonths) {//Аннуитетный расчет платежа
        BigDecimal monthlyRate = annualRate.divide(BigDecimal.valueOf(12), MathContext.DECIMAL128);
        BigDecimal onePlusRateToPowerTerm = BigDecimal.ONE.add(monthlyRate).pow(termMonths, MathContext.DECIMAL128);
        BigDecimal numerator = loanAmount.multiply(monthlyRate).multiply(onePlusRateToPowerTerm);
        BigDecimal denominator = onePlusRateToPowerTerm.subtract(BigDecimal.ONE);
        return numerator.divide(denominator, 2, RoundingMode.HALF_UP);
    }

    private BigDecimal calculateDifferentiatedMonthlyPayment(BigDecimal loanAmount, BigDecimal annualRate, int termMonths) {//Дифференцированный расчет платежа
        BigDecimal monthlyRate = annualRate.divide(BigDecimal.valueOf(12), MathContext.DECIMAL128);
        BigDecimal principalPayment = loanAmount.divide(BigDecimal.valueOf(termMonths), MathContext.DECIMAL128);
        BigDecimal interestPayment = loanAmount.multiply(monthlyRate);
        return principalPayment.add(interestPayment).setScale(2, RoundingMode.HALF_UP);
    }
}
