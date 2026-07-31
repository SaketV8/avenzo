package com.maurya.avenzo.dto.response;

import com.maurya.avenzo.constant.EventMemberRole;

import java.time.LocalDateTime;

public record EventMemberResponseDto(
        Long id,

        Long userId,
        String userName,
        String userEmail,
        String userPhone,
        String userProfilePicture,

        EventMemberRole eventMemberRole,

        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
