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
public class ExperienceDTO {
    private Long id;

    @NotBlank(message = "Company name is required")
    private String company;

    @NotBlank(message = "Role is required")
    private String role;

    private String location;
    private LocalDate startDate;
    private LocalDate endDate;
    private Boolean current;
    private String description;
    private String responsibilities;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
