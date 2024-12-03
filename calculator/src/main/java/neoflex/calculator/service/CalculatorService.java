/**
 * Пакет сервисов для калькулятора кредитных предложений.
 */
package neoflex.calculator.service;

import lombok.Getter;
import neoflex.calculator.dto.*;
import neoflex.calculator.enums.Gender;
import neoflex.calculator.util.AgeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

/**
 * Сервис для генерации кредитных предложений.
 */
@Service
public class CalculatorService {
    private static final Logger logger = LoggerFactory.getLogger(CalculatorService.class);

    @Getter
    @Value("${base.interest.rate}")
    private BigDecimal baseInterestRate;

    private static final BigDecimal INSURANCE_DISCOUNT = BigDecimal.valueOf(0.03);
    private static final BigDecimal INSURANCE_COST_RATE = BigDecimal.valueOf(0.01);
    private static final BigDecimal SALARY_CLIENT_DISCOUNT = BigDecimal.valueOf(0.01);

    /**
     * Генерирует список кредитных предложений на основе запроса.
     *
     * @param request запрос на кредитное предложение
     * @return список кредитных предложений
     */
    public List<LoanOfferDto> generateLoanOffers(LoanStatementRequestDto request) {
        logger.info("Генерация кредитных предложений для запроса: {}", request);

        int age = AgeUtils.calculateAge(request.getBirthDate(), LocalDate.now());
        logger.debug("Рассчитанный возраст клиента: {}", age);

        if (age < 20 || age > 65) {
            logger.warn("Возраст клиента {} не подходит для кредита. Заявка отклонена.", age);
            throw new IllegalArgumentException("Отказ: возраст клиента должен быть от 20 до 65 лет.");
        }

        List<LoanOfferDto> offers = new ArrayList<>();

        offers.add(createLoanOffer(request, false, false));
        offers.add(createLoanOffer(request, false, true));
        offers.add(createLoanOffer(request, true, false));
        offers.add(createLoanOffer(request, true, true));

        offers.sort(Comparator.comparing(LoanOfferDto::getRate));

        logger.info("Сгенерировано {} кредитных предложений.", offers.size());
        return offers;
    }

    /**
     * Создает кредитное предложение.
     *
     * @param request            запрос на кредитное предложение
     * @param isInsuranceEnabled  включено ли страхование
     * @param isSalaryClient      является ли клиент зарплатным клиентом
     * @return кредитное предложение
     */
    private LoanOfferDto createLoanOffer(LoanStatementRequestDto request, boolean isInsuranceEnabled, boolean isSalaryClient) {
        logger.debug("Создание кредитного предложения с insuranceEnabled: {}, salaryClient: {}", isInsuranceEnabled, isSalaryClient);

        BigDecimal interestRate = baseInterestRate;

        BigDecimal loanAmount = request.getAmount();

        //Применение правил скоринга по страховке
        if (isInsuranceEnabled) {
            interestRate = interestRate.subtract(INSURANCE_DISCOUNT);
            BigDecimal insuranceCost = loanAmount.multiply(INSURANCE_COST_RATE);
            loanAmount = loanAmount.add(insuranceCost);
        }
        //Применение правил скоринга по зарплатному клиенту
        if (isSalaryClient) {
            interestRate = interestRate.subtract(SALARY_CLIENT_DISCOUNT);
        }

        BigDecimal monthlyPayment = calculateAnnuityMonthlyPayment(loanAmount, interestRate, request.getTerm());
        BigDecimal totalAmount = monthlyPayment.multiply(BigDecimal.valueOf(request.getTerm()));

        LoanOfferDto loanOfferDto = new LoanOfferDto(
                UUID.randomUUID(),
                request.getAmount(),
                request.getTerm(),
                monthlyPayment,
                totalAmount,
                interestRate,
                isInsuranceEnabled,
                isSalaryClient
        );

        logger.debug("Создано кредитное предложение: {}", loanOfferDto);
        return loanOfferDto;
    }

    /**
     * Рассчитывает аннуитетный ежемесячный платеж.
     *
     * @param loanAmount  сумма кредита
     * @param rate        процентная ставка
     * @param termMonths  срок кредита в месяцах
     * @return аннуитетный ежемесячный платеж
     */
    BigDecimal calculateAnnuityMonthlyPayment(BigDecimal loanAmount, BigDecimal rate, int termMonths) {
        logger.debug("Расчет аннуитетного ежемесячного платежа для loanAmount: {}, rate: {}, termMonths: {}", loanAmount, rate, termMonths);

        BigDecimal monthlyRate = rate.divide(BigDecimal.valueOf(12), MathContext.DECIMAL128);
        BigDecimal onePlusRateToPowerTerm = BigDecimal.ONE.add(monthlyRate).pow(termMonths, MathContext.DECIMAL128);
        BigDecimal numerator = loanAmount.multiply(monthlyRate).multiply(onePlusRateToPowerTerm);
        BigDecimal denominator = onePlusRateToPowerTerm.subtract(BigDecimal.ONE);
        BigDecimal annuityPayment = numerator.divide(denominator, 2, RoundingMode.HALF_UP);

        logger.debug("Рассчитан аннуитетный ежемесячный платеж: {}", annuityPayment);
        return annuityPayment;
    }

    /**
     * Рассчитывает детали кредита на основе данных скоринга.
     *
     * @param scoringData данные для скоринга
     * @return детали кредита
     */
    public CreditDto calculateCredit(ScoringDataDto scoringData) {
        logger.info("Расчет кредита для данных скоринга: {}", scoringData);

        BigDecimal rate = baseInterestRate;

        //Применение правил скоринга по страховке
        if (scoringData.getIsInsuranceEnabled()) {
            rate = rate.subtract(INSURANCE_DISCOUNT);
            scoringData.setAmount(scoringData.getAmount().add(scoringData.getAmount().multiply(INSURANCE_COST_RATE)));
        }

        //Применение правил скоринга по зарплатному клиенту
        if (scoringData.getIsSalaryClient()) {
            rate = rate.subtract(SALARY_CLIENT_DISCOUNT);
        }
        // Применение правил скоринга по статусу занятости
        switch (scoringData.getEmployment().getEmploymentStatus()) {
            case UNEMPLOYED:
                logger.warn("Заявка отклонена: Статус - безработный.");
                throw new IllegalArgumentException("Отказ: статус - безработный.");
            case SELF_EMPLOYED:
                rate = rate.add(BigDecimal.valueOf(0.02));
                break;
            case BUSINESS_OWNER:
                rate = rate.add(BigDecimal.valueOf(0.01));
                break;
            default:
                break;
        }

        // Применение правил скоринга по позиции на работе
        switch (scoringData.getEmployment().getPosition()) {
            case MIDDLE_MANAGER:
                rate = rate.subtract(BigDecimal.valueOf(0.02));
                break;
            case TOP_MANAGER:
                rate = rate.subtract(BigDecimal.valueOf(0.03));
                break;
            default:
                break;
        }

        // Применение правил скоринга по сумме займа
        if (scoringData.getAmount().compareTo(scoringData.getEmployment().getSalary().multiply(BigDecimal.valueOf(24))) > 0) {
            logger.warn("Заявка отклонена: Сумма займа превышает 24 зарплаты.");
            throw new IllegalArgumentException("Отказ: сумма займа больше, чем 24 зарплат.");
        }

        // Применение правил скоринга по семейному положению
        switch (scoringData.getMaritalStatus()) {
            case MARRIED:
                rate = rate.subtract(BigDecimal.valueOf(0.03));
                break;
            case DIVORCED:
                rate = rate.add(BigDecimal.valueOf(0.01));
                break;
            default:
                break;
        }

        // Применение правил скоринга по полу
        int age = calculateAge(scoringData.getBirthdate(), LocalDate.now());
        if (scoringData.getGender() == Gender.FEMALE && age >= 32 && age <= 60) {
            rate = rate.subtract(BigDecimal.valueOf(0.03));
        } else if (scoringData.getGender() == Gender.MALE && age >= 30 && age <= 55) {
            rate = rate.subtract(BigDecimal.valueOf(0.03));
        } else if (scoringData.getGender() == Gender.NON_BINARY) {
            rate = rate.add(BigDecimal.valueOf(0.07));
        }

        // Применение правил скоринга по стажу работы
        if (scoringData.getEmployment().getWorkExperienceTotal() < 18 || scoringData.getEmployment().getWorkExperienceCurrent() < 3) {
            logger.warn("Заявка отклонена: Недостаточный стаж работы.");
            throw new IllegalArgumentException("Отказ: стаж работы менее 18 месяцев или текущий стаж менее 3 месяцев.");
        }

        // Расчет аннуитентного платежа и ПСК
        BigDecimal monthlyPayment = calculateAnnuityMonthlyPayment(scoringData.getAmount(), rate, scoringData.getTerm());
        BigDecimal psk = monthlyPayment.multiply(BigDecimal.valueOf(scoringData.getTerm()));
        List<PaymentScheduleElementDto> paymentSchedule = calculateAnnuityPaymentSchedule(scoringData.getAmount(), rate, scoringData.getTerm());

        // Создание и возвращение CreditDto
        CreditDto creditDto = CreditDto.builder()
                .amount(scoringData.getAmount())
                .term(scoringData.getTerm())
                .rate(rate)
                .monthlyPayment(monthlyPayment)
                .psk(psk)
                .isInsuranceEnabled(scoringData.getIsInsuranceEnabled())
                .isSalaryClient(scoringData.getIsSalaryClient())
                .paymentSchedule(paymentSchedule)
                .build();

        logger.info("Рассчитанные данные кредита: {}", creditDto);
        return creditDto;
    }

    /**
     * Рассчитывает график аннуитетных платежей.
     *
     * @param loanAmount сумма кредита
     * @param rate        процентная ставка
     * @param term        срок кредита в месяцах
     * @return график аннуитетных платежей
     */
    private List<PaymentScheduleElementDto> calculateAnnuityPaymentSchedule(BigDecimal loanAmount, BigDecimal rate, int term) {
        logger.debug("Расчет графика аннуитетных платежей для loanAmount: {}, rate: {}, term: {}", loanAmount, rate, term);

        List<PaymentScheduleElementDto> paymentSchedule = new ArrayList<>();
        BigDecimal monthlyRate = rate.divide(BigDecimal.valueOf(12), MathContext.DECIMAL128);
        BigDecimal remainingPrincipal = loanAmount;
        BigDecimal monthlyPayment = calculateAnnuityMonthlyPayment(loanAmount, rate, term);

        for (int i = 0; i < term; i++) {
            BigDecimal interestPayment = remainingPrincipal.multiply(monthlyRate);
            BigDecimal principalPayment = monthlyPayment.subtract(interestPayment);
            remainingPrincipal = remainingPrincipal.subtract(principalPayment);

            PaymentScheduleElementDto element = PaymentScheduleElementDto.builder()
                    .number(i + 1)
                    .date(LocalDate.now().plusMonths(i))
                    .totalPayment(monthlyPayment)
                    .interestPayment(interestPayment)
                    .debtPayment(principalPayment)
                    .remainingDebt(remainingPrincipal)
                    .build();

            paymentSchedule.add(element);
        }

        logger.debug("Рассчитан график аннуитетных платежей: {}", paymentSchedule);
        return paymentSchedule;
    }
}
