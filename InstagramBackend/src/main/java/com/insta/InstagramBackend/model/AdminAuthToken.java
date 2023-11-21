package com.insta.InstagramBackend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class AdminAuthToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long adminTokenId;
    private String adminTokenValue;
    private LocalDateTime adminTokenCreationTimeStamp;

    @OneToOne
    @JoinColumn(name = "fk_admin_id")
    Admin admin;

    public AdminAuthToken(Admin admin) {
        this.admin = admin;
        this.adminTokenValue = UUID.randomUUID().toString();
        this.adminTokenCreationTimeStamp = LocalDateTime.now();
    }
}
