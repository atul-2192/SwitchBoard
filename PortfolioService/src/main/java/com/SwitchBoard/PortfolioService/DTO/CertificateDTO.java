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
public class CertificateDTO {
    private Long id;

    @NotBlank(message = "Certificate title is required")
    private String title;

    @NotBlank(message = "Issuer is required")
    private String issuer;

    private LocalDate issueDate;
    private LocalDate expiryDate;
    private String credentialId;
    private String credentialUrl;
    private String certificateImageUrl;
    private String description;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
