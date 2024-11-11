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

    public List<LoanOfferDto> generateLoanOffers(LoanStatementRequestDto request) {

    }

    private LoanOfferDto createLoanOffer(LoanStatementRequestDto request, boolean isInsuranceEnabled, boolean isSalaryClient) {


        return new LoanOfferDto(
                UUID.randomUUID(),
                request.getAmount(),
                loanAmount,
                request.getTerm(),
                monthlyPayment,
                interestRate,
                isInsuranceEnabled,
                isSalaryClient
        );
    }

    private BigDecimal calculateMonthlyPayment(BigDecimal loanAmount, BigDecimal annualRate, int termMonths) {//Аннуитетный расчет платежа

    }
}
