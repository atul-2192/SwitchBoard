package com.SwitchBoard.PortfolioService.Entity;


import jakarta.persistence.*;
import lombok.*;
import java.util.*;

@Entity
@Table(name = "assignments")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Assignment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String description;
    private Date deadline;

    private Date createdAt;
}
