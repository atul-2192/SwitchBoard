package com.SwitchBoard.AiService.Domain;



import lombok.*;
import jakarta.validation.constraints.*;
import java.util.List;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Assignment {
    @NotBlank private String title;
    @NotBlank private String description;
    @Min(1) private int deadline; // total days for assignment

    // Computed absolute ISO date (set by DeadlinePlanner)
    private String deadlineDateIso;

    @Builder.Default
    private List<Task> tasks = List.of();
}
