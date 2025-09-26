package com.SwitchBoard.AiService.Domain;



import lombok.*;
import jakarta.validation.constraints.*;

import java.util.List;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Task {
    @NotBlank private String title;
    @NotBlank private String description;

    @Min(1) private int rewardPoints;         // points
    @NotNull private PriorityColor titleColor; // color
    @Min(1) private int deadline;             // d: days to complete this task (relative duration)
    @Min(1) private int order;                // 1 means first
    @Builder.Default
    private List<Subtopic> subtopics = List.of();

    // Computed absolute ISO date (set by DeadlinePlanner)
    private String deadlineDateIso;
}
