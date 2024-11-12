package neoflex.calculator.dto;

import lombok.Getter;

import java.math.BigDecimal;

public class EmploymentDto {

    public enum EmploymentStatus {
        EMPLOYED,
        UNEMPLOYED,
        SELF_EMPLOYED,
        BUSINESS_OWNER,
        RETIRED
    }

    public enum Position {
        MANAGER,
        DEVELOPER,
        ANALYST,
        OTHER,
        MIDDLE_MANAGER,
        TOP_MANAGER;
    }
    @Getter
    private EmploymentStatus employmentStatus;
    @Getter
    private String employerINN;
    @Getter
    private BigDecimal salary;
    @Getter
    private Position position;
    @Getter
    private Integer workExperienceTotal;
    @Getter
    private Integer workExperienceCurrent;
}