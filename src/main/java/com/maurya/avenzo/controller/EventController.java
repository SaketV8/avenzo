package com.maurya.avenzo.controller;

import com.maurya.avenzo.constant.UrlConstant;
import com.maurya.avenzo.dto.request.EventRequestDto;
import com.maurya.avenzo.dto.response.EventResponseDto;
import com.maurya.avenzo.service.EventService;
import io.swagger.v3.oas.annotations.Operation;
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
public class EventController {

    private final EventService eventService;

    final String events_base = UrlConstant.EVENTS_URL;

    @GetMapping(events_base)
    public ResponseEntity<List<EventResponseDto>> getAllEvents() {
        return ResponseEntity.status(HttpStatus.OK).body(eventService.getAllEvents());
    }

    @GetMapping(events_base + "/{eventId}")
    public ResponseEntity<EventResponseDto> getEvent(@PathVariable Long eventId) {
        return ResponseEntity.status(HttpStatus.OK).body(eventService.getEvents(eventId));
    }

    @SecurityRequirement(name = "bearerAuth")
    @PostMapping(events_base)
    public ResponseEntity<EventResponseDto> createEvent(@Valid @RequestBody EventRequestDto eventRequestDto) {
        System.out.println("🐸 Event Create called");
        return ResponseEntity.status(HttpStatus.CREATED).body(eventService.createEvent(eventRequestDto));
    }

    @SecurityRequirement(name = "bearerAuth")
    @PutMapping(events_base + "/{eventId}")
    public ResponseEntity<EventResponseDto> updateEvent(@PathVariable Long eventId, @Valid @RequestBody EventRequestDto eventRequestDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(eventService.updateEvent(eventId, eventRequestDto));
    }

    @SecurityRequirement(name = "bearerAuth")
    @DeleteMapping(events_base + "/{eventId}")
    public ResponseEntity<EventResponseDto> deleteEvent(@PathVariable Long eventId) {
        return ResponseEntity.status(HttpStatus.OK).body(eventService.deleteEvent(eventId));
    }

    /*
    * event member controller
    */

}
