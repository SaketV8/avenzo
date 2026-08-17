package com.maurya.avenzo.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "sessions")
public class SessionEntity {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @ManyToOne(fetch = FetchType.LAZY, optional = false)
        @JoinColumn(name = "user_id")
        private UserEntity user;

        @Column(nullable = false, unique = true)
        private String refreshTokenHash;

        @Column(nullable = false)
        private LocalDateTime expiresAt;

        @Column(nullable = false)
        private boolean revoked = false;

        @CreationTimestamp
        private LocalDateTime createdAt;
}
