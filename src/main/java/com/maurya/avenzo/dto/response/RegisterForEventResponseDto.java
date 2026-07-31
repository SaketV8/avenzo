package com.maurya.avenzo.dto.response;

import com.maurya.avenzo.constant.RegistrationStatus;

import java.time.LocalDateTime;

public record RegisterForEventResponseDto(
        Long id,

        Long userId,
        String userName,
        String userEmail,
        String userPhone,
        String userProfilePicture,

        RegistrationStatus status,

        String ticketNumber,

        LocalDateTime createdAt,
        LocalDateTime updatedAt

) {
}
