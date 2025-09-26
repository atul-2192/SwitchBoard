package com.SwitchBoard.PortfolioService.DTO;


import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EducationDTO {
    private Long id;

    @NotBlank(message = "Institution name is required")
    private String institution;

    @NotBlank(message = "Degree is required")
    private String degree;

    private String fieldOfStudy;
    private Double grade;
    private LocalDate startDate;
    private LocalDate endDate;
    private Boolean ongoing;
    private String description;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
