package com.maurya.avenzo.dto.response;

public record PrivateUserResponseDto(Long id,
                                     String name,
                                     String email,
                                     String phone,
                                     String profilePicture) {
}
