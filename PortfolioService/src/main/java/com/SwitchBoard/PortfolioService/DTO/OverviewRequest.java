package com.SwitchBoard.PortfolioService.DTO;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OverviewRequest {
    @NotBlank(message = "Overview content is required")
    private String overview;
}