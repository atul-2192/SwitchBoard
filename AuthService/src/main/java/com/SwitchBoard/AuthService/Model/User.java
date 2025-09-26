package com.SwitchBoard.AuthService.Model;

import com.SwitchBoard.AuthService.Config.NanoId;
import com.SwitchBoard.AuthService.DTO.USER_ROLE;
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
@Entity
@Table(
        name = "consumers",
        indexes = {
                @Index(name = "idx_user_email", columnList = "email", unique = true)
        }
)
@Builder
public class User {

    @Id
    @NanoId
    @Column(length = 16, nullable = false, updatable = false, unique = true)
    private String id;

    private String name;

    @Column(unique = true, nullable = false)
    private String email;

    @Enumerated(EnumType.STRING)
    private List<USER_ROLE> userRole = Collections.singletonList(USER_ROLE.USER);

    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt = new Date();

    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt = new Date();
}
