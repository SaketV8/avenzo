package com.maurya.avenzo.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EventCheckInRequestDto {

    @NotBlank(message = "TicketNumber must be valid")
    private String ticketNumber;
}
