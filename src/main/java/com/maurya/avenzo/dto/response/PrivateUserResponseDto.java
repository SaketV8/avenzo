package com.maurya.avenzo.dto.response;

import com.maurya.avenzo.role.Role;

public record PrivateUserResponseDto(Long id,
                                     String name,
                                     String email,
                                     String phone,
                                     String profilePicture,
                                     Role role) {
}
