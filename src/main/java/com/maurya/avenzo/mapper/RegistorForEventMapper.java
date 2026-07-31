package com.maurya.avenzo.mapper;

import com.maurya.avenzo.dto.response.RegisterForEventResponseDto;
import com.maurya.avenzo.entity.RegistrationEntity;
import org.springframework.stereotype.Component;

@Component
public class RegistorForEventMapper {

    public RegisterForEventResponseDto toRegisterForEventDto(RegistrationEntity registrationEntity) {
        return new RegisterForEventResponseDto(
                registrationEntity.getId(),
                registrationEntity.getUser().getId(),
                registrationEntity.getUser().getName(),
                registrationEntity.getUser().getEmail(),
                registrationEntity.getUser().getPhone(),
                registrationEntity.getUser().getProfilePicture(),
                registrationEntity.getStatus(),
                registrationEntity.getTicketNumber(),

                registrationEntity.getCreatedAt(),
                registrationEntity.getUpdatedAt()
        );
    }
}
