package com.maurya.avenzo.controller;

import com.maurya.avenzo.constant.UrlConstant;
import com.maurya.avenzo.dto.request.EventMemberRequestDto;
import com.maurya.avenzo.dto.response.EventMemberResponseDto;
import com.maurya.avenzo.service.EventMemberService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(UrlConstant.BASE_URL)
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class EventMemberController {

    final String events_base = UrlConstant.EVENTS_URL;
    final String members_base = UrlConstant.MEMBERS_URL;

    private final EventMemberService eventMemberService;

    @GetMapping(events_base + "/{eventId}" + members_base)
    public ResponseEntity<List<EventMemberResponseDto>> getEventMembers(@PathVariable Long eventId) {
        return ResponseEntity.ok(eventMemberService.getEventMember(eventId));
    }

    @PostMapping(events_base + "/{eventId}" + members_base + "/{userId}")
    public ResponseEntity<EventMemberResponseDto> addEventMembers(@PathVariable Long eventId, @PathVariable Long userId) {
        return ResponseEntity.ok(eventMemberService.addEventMember(eventId, userId));
    }

    @DeleteMapping(events_base + "/{eventId}" + members_base + "/{userId}")
    public ResponseEntity<EventMemberResponseDto> deleteEventMembers(@PathVariable Long eventId, @PathVariable Long userId) {
        return ResponseEntity.ok(eventMemberService.deleteEventMember(eventId, userId));
    }

    @PutMapping(events_base + "/{eventId}" + members_base + "/{userId}")
    public ResponseEntity<EventMemberResponseDto> updateEventMembers(@Valid @PathVariable Long eventId, @Valid @PathVariable Long userId, @Valid @RequestBody EventMemberRequestDto eventMemberRequestDto) {
        return ResponseEntity.ok(eventMemberService.updateEventMember(eventId, userId, eventMemberRequestDto));
    }
}
