package neoflex.deal.mapper;

import neoflex.dto.EmploymentDto;
import neoflex.deal.entity.Employment;

public class EmploymentMapper {

    private EmploymentMapper() {
    }

    public static Employment toEntity(EmploymentDto dto) {
        if (dto == null) {
            return null;
        }
        return Employment.builder()
                .status(dto.getEmploymentStatus())
                .employerInn(dto.getEmployerINN())
                .salary(dto.getSalary())
                .position(dto.getPosition())
                .workExperienceTotal(dto.getWorkExperienceTotal())
                .workExperienceCurrent(dto.getWorkExperienceCurrent())
                .build();
    }
}
