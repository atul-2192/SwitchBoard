package com.SwitchBoard.PortfolioService.DTO;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PortfolioDTO {
    private Long id;

    @NotNull(message = "Email ID is required")
    private Long emailId;

    @NotBlank(message = "Full name is required")
    private String fullName;

    private String bio;
    private String profileImageUrl;
    private String socialLinks;
    private String overview;

    private List<SkillDTO> skills = new ArrayList<>();
    private List<ProjectDTO> projects = new ArrayList<>();
    private List<EducationDTO> educations = new ArrayList<>();
    private List<ExperienceDTO> experiences = new ArrayList<>();
    private List<CertificateDTO> certificates = new ArrayList<>();
    private List<AchievementDTO> achievements = new ArrayList<>();

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
