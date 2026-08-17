package com.maurya.avenzo.repository;

import com.maurya.avenzo.constant.EventMemberRole;
import com.maurya.avenzo.entity.EventMemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface EventMemberRepository extends JpaRepository<EventMemberEntity, Long> {
    boolean existsByEventIdAndUserIdAndRole(
            Long eventId,
            Long userId,
            EventMemberRole role
    );

    boolean existsByEventIdAndUserIdAndRoleIn(
            Long eventId,
            Long userId,
            Collection<EventMemberRole> roles);

    List<EventMemberEntity> findByEventId(Long eventId);

    /*List<EventMemberEntity> findByEventIdAndRoleIn(
            Long eventId,
            EventMemberRole role
    );*/

    Optional<EventMemberEntity> findByEventIdAndUserId(Long eventId, Long userId);

    boolean existsByEventIdAndUserId(Long eventId, Long userId);

    Long countByEventIdAndRole(Long eventId, EventMemberRole role);

    List<EventMemberEntity> findAllByUserIdAndRole(Long userId, EventMemberRole role);
}
