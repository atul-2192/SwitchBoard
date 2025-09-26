package com.SwitchBoard.AiService.Dto;


import com.SwitchBoard.AiService.Domain.Proficiency;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoadmapRequest {
    @NotBlank private String subject;
    @Min(1) @Max(365) private int totalDays;       // T
    @Min(1) @Max(16)  private int dailyHours;      // H
    @NotNull private Proficiency proficiency;      // P (BEGINNER/INTERMEDIATE/EXPERT)


}
