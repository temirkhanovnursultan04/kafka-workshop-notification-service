package kz.nurs.study.kafkaworkshopnotificationservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Решение по кредиту, которое присылает credit-service — по REST или через Kafka.
 * Поля совпадают с CreditResponseDto из credit-service: JSON один и тот же.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreditDecisionDto {

    private String name;
    private String surname;
    private BigDecimal creditSum;
    private Integer creditPeriodInMonths;
    private BigDecimal salary;
    private Boolean isApproved;
    private BigDecimal monthlyPayment;
}
