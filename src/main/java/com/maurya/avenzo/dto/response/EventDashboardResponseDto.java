package com.maurya.avenzo.dto.response;

public record EventDashboardResponseDto(
        Long capacity,
        Long availableSeats,

        Long Registered,
        Long Attended,

        Long Organizers,

        Long Volunteers

) {
}
