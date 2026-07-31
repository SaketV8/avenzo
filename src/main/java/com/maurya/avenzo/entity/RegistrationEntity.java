package com.maurya.avenzo.entity;

import com.maurya.avenzo.constant.RegistrationStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(
        name = "registrations",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"user_id", "event_id"}),
                @UniqueConstraint(columnNames = {"ticket_number"})
        }

)
public class RegistrationEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;
    /*private Long UserId;*/

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "event_id", nullable = false)
    private EventEntity event;
    /*private Long EventId;*/

    @Column(name = "ticket_number", nullable = false, unique = true, updatable = false)
    private String ticketNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RegistrationStatus status;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
