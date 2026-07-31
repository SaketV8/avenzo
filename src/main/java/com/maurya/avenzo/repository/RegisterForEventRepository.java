package com.maurya.avenzo.repository;

import com.maurya.avenzo.constant.RegistrationStatus;
import com.maurya.avenzo.entity.RegistrationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RegisterForEventRepository extends JpaRepository<RegistrationEntity, Long> {
    boolean existsByUserIdAndEventId(Long userId, Long eventId);

    List<RegistrationEntity> findByUserId(Long userId);
    List<RegistrationEntity> findByEventId(Long eventId);

    Optional<RegistrationEntity> findByUserIdAndEventId(Long userId, Long eventId);

    Optional<RegistrationEntity> findByTicketNumber(String ticketNumber);

    List<RegistrationEntity> findByEventIdAndStatus(Long eventId, RegistrationStatus status);

    Long countByEventIdAndStatus(Long eventId, RegistrationStatus status);
}
