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
public class ProjectDTO {
    private Long id;

    @NotBlank(message = "Project name is required")
    private String name;

    private String description;
    private String url;
    private String imageUrl;
    private String technologies;
    private LocalDate startDate;
    private LocalDate endDate;
    private Boolean ongoing;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

