package com.maurya.avenzo.dto.request;

import com.maurya.avenzo.constant.EventMemberRole;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EventMemberRequestDto {

    @NotNull(message = "Role is required")
    private EventMemberRole role;
}
