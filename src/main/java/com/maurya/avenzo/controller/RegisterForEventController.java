package com.maurya.avenzo.controller;

import com.maurya.avenzo.constant.UrlConstant;

import com.maurya.avenzo.dto.request.EventCheckInRequestDto;
import com.maurya.avenzo.dto.response.EventDashboardResponseDto;
import com.maurya.avenzo.dto.response.EventResponseDto;
import com.maurya.avenzo.dto.response.RegisterForEventResponseDto;
import com.maurya.avenzo.service.RegisterForEventService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(UrlConstant.BASE_URL)
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class RegisterForEventController {

    final String events_base = "/events";
    final String register_base = "/registrations";

    private final RegisterForEventService registerForEventService;

    @PostMapping(events_base + "/{eventId}/register")
    public ResponseEntity<RegisterForEventResponseDto>  registerForEvent(@PathVariable Long eventId){
        return ResponseEntity.status(HttpStatus.CREATED).body(registerForEventService.registerForEvent(eventId));
    }

    @DeleteMapping(events_base + "/{eventId}/register")
    public ResponseEntity<RegisterForEventResponseDto>  deregisterForEvent(@PathVariable Long eventId){
        return ResponseEntity.status(HttpStatus.CREATED).body(registerForEventService.deregisterForEvent(eventId));
    }

    @GetMapping(register_base + "/me")
    public ResponseEntity<List<EventResponseDto>> getUserRegisteredEvents() {
        return  ResponseEntity.ok(registerForEventService.getUserRegisteredEvents());
    }

    // get all registration
    @GetMapping(events_base + "/{eventId}" + register_base)
    public ResponseEntity<List<RegisterForEventResponseDto>> getEventRegistrations(@PathVariable Long eventId){
        return ResponseEntity.ok(registerForEventService.getEventRegistrations(eventId));
    }

    @PostMapping(events_base + "/{eventId}" + "/check-in")
    public ResponseEntity<RegisterForEventResponseDto> checkInForEvent(@PathVariable Long eventId, @Valid @RequestBody EventCheckInRequestDto eventCheckInRequestDto){
        return ResponseEntity.ok(registerForEventService.checkInForEvent(eventId, eventCheckInRequestDto));
    }

    @GetMapping(events_base + "/{eventId}" + "/attendees")
    public ResponseEntity<List<RegisterForEventResponseDto>> attendeesForEvent(@PathVariable Long eventId){
        return ResponseEntity.ok(registerForEventService.attendeesForEvent(eventId));
    }

    @GetMapping(events_base + "/{eventId}" + "/dashboard")
    public ResponseEntity<EventDashboardResponseDto> dashboardForEvent(@PathVariable Long eventId){
        return ResponseEntity.ok(registerForEventService.dashboardForEvent(eventId));
    }


}
