/**
 * Пакет DTO для калькулятора кредитных предложений.
 */
package neoflex.calculator.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

/**
 * DTO для данных кредита.
 */
@Getter
@Setter
public class CreditDto {

    private BigDecimal amount;
    private Integer term;
    private BigDecimal rate;
    //Добавлено разделение на Аннуитентный и Дифференцированный платежи
    private BigDecimal annuityMonthlyPayment;
    private BigDecimal annuityPsk;
    private BigDecimal differentiatedMonthlyPayment;
    private BigDecimal differentiatedPsk;
    private Boolean isInsuranceEnabled;
    private Boolean isSalaryClient;
    private List<PaymentScheduleElementDto> annuityPaymentSchedule;
    private List<PaymentScheduleElementDto> differentiatedPaymentSchedule;
}
