package com.maurya.avenzo.mapper;

import com.maurya.avenzo.dto.response.EventResponseDto;
import com.maurya.avenzo.entity.EventEntity;
import jdk.jfr.Event;
import org.springframework.stereotype.Component;

@Component
public class EventMapper {

    public EventResponseDto toEventResponseDto(EventEntity event) {
        return new EventResponseDto(
                event.getId(),
                event.getTitle(),
                event.getDescription(),
                event.getVenue(),
                event.getCity(),
                event.getMapLink(),
                event.getStartTime(),
                event.getEndTime(),
                event.getRegistrationDeadline(),
                event.getCapacity(),
                event.getAvailableSeats(),
                event.getBannerImage(),
                event.getStatus(),
                event.getCategory().getId(),
                event.getCategory().getName(),
                event.getCreatedAt(),
                event.getUpdatedAt()
        );
    }
}
