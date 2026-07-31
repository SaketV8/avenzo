package com.maurya.avenzo.mapper;

import com.maurya.avenzo.dto.response.EventMemberResponseDto;
import com.maurya.avenzo.entity.EventMemberEntity;
import org.springframework.stereotype.Component;

@Component
public class EventMemberMapper {

    public EventMemberResponseDto toEventMemberResponseDto(EventMemberEntity eventMemberEntity) {

        return new EventMemberResponseDto(
                eventMemberEntity.getId(),
                eventMemberEntity.getUser().getId(),
                eventMemberEntity.getUser().getName(),
                eventMemberEntity.getUser().getEmail(),
                eventMemberEntity.getUser().getPhone(),
                eventMemberEntity.getUser().getProfilePicture(),
                eventMemberEntity.getRole(),

                eventMemberEntity.getCreatedAt(),
                eventMemberEntity.getUpdatedAt()
        );
    }
}
