package com.maurya.avenzo.service;

import com.maurya.avenzo.dto.response.PrivateUserResponseDto;
import com.maurya.avenzo.entity.EventMemberEntity;
import com.maurya.avenzo.entity.UserEntity;
import com.maurya.avenzo.mapper.UserMapper;
import com.maurya.avenzo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public List<PrivateUserResponseDto> getAllUsers() {
        // only admin can access this URL
        List<UserEntity> userEntities = userRepository.findAll();
        List<PrivateUserResponseDto> privateUserResponseDtoList = new ArrayList<>();

        for (UserEntity userEntity : userEntities) {
            PrivateUserResponseDto privateUserResponseDto = userMapper.toPrivateUserResponseDto(userEntity);
            privateUserResponseDtoList.add(privateUserResponseDto);
        }

        return privateUserResponseDtoList;
    }
}
