package com.maurya.avenzo.repository;

import com.maurya.avenzo.entity.SessionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SessionRepository extends JpaRepository<SessionEntity, Long> {

    Optional<SessionEntity> findByRefreshTokenHash(String refreshTokenHash);

}
