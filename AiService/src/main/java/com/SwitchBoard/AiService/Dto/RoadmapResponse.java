package com.SwitchBoard.AiService.Dto;

import com.SwitchBoard.AiService.Domain.Assignment;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class RoadmapResponse {

        private String title;
        private String description;
        private LocalDateTime deadline;
        private List<Task> tasks;


        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        public static class Task {
            private String title;
            private String description;
            private int rewardPoints;
            private LocalDateTime deadline;
            private String titleColor;
            private int order;

        }
    }

