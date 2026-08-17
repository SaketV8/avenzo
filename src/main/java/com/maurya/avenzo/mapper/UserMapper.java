package com.maurya.avenzo.mapper;

import com.maurya.avenzo.dto.response.PrivateUserResponseDto;
import com.maurya.avenzo.entity.UserEntity;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public PrivateUserResponseDto toPrivateUserResponseDto(UserEntity userEntity) {

        return new PrivateUserResponseDto(
                userEntity.getId(),
                userEntity.getName(),
                userEntity.getEmail(),
                userEntity.getPhone(),
                userEntity.getProfilePicture(),
                userEntity.getRole()
        );
    }
}
