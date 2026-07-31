package com.maurya.avenzo.dto.response;

import com.maurya.avenzo.constant.EventStatus;

import java.time.LocalDateTime;

public record EventResponseDto(
        Long id,
        String title,
        String description,
        String venue,
        String city,
        String mapLink,
        LocalDateTime startTime,
        LocalDateTime endTime,
        LocalDateTime registrationDeadline,
        Integer capacity,
        Integer availableSeats,
        String bannerImage,
        EventStatus status,
        Long categoryId,
        String categoryName,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
