package com.SwitchBoard.AccountService.Entity;

import com.SwitchBoard.AccountService.Config.NanoId;
import com.SwitchBoard.AccountService.Dto.USER_ROLE;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(
        name = "account",
        indexes = {
                     @Index(name = "idx_account_email", columnList = "email", unique = true)
        }
)
public class Account {

    @Id
    @NanoId
    @Column(length = 16, nullable = false, updatable = false, unique = true)
    private String id;


    private String name;

    @Column(unique = true, nullable = false)
    private String email;

    private String mobile;
    private String linkedinUrl;
    private String githubUrl;
    private String leetcodeUrl;
    private String cvPath;

    private Date deadline;
    private String aimRole;
    @Column(name = "current_role_name")
    private String currentRole;

    private int totalRewardPoints=0;
    private int taskAssignedCount=0;
    private int taskCompletedCount=0;

    @Enumerated(EnumType.STRING)
    private List<USER_ROLE> userRole = Collections.singletonList(USER_ROLE.USER);

    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt = new Date();

    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt = new Date();

}
