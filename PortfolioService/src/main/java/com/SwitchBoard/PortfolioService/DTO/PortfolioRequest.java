package com.SwitchBoard.PortfolioService.DTO;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PortfolioRequest {
    @NotNull(message = "Email ID is required")
    private String emailId;

    @NotBlank(message = "Full name is required")
    private String fullName;

    private String bio;
    private String profileImageUrl;
    private String socialLinks;
    private String overview;
}
