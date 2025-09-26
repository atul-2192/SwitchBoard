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
public class AchievementDTO {
    private Long id;

    @NotBlank(message = "Achievement title is required")
    private String title;

    private LocalDate date;
    private String issuer;
    private String description;
    private String url;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

