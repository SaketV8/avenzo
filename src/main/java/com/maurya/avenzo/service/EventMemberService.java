package com.maurya.avenzo.service;

import com.maurya.avenzo.constant.EventMemberRole;
import com.maurya.avenzo.dto.request.EventMemberRequestDto;
import com.maurya.avenzo.dto.response.EventMemberResponseDto;
import com.maurya.avenzo.entity.EventEntity;
import com.maurya.avenzo.entity.EventMemberEntity;
import com.maurya.avenzo.entity.UserEntity;
import com.maurya.avenzo.exception.ApiException;
import com.maurya.avenzo.exception.ErrorCode;
import com.maurya.avenzo.mapper.EventMemberMapper;
import com.maurya.avenzo.repository.EventMemberRepository;
import com.maurya.avenzo.repository.EventRespository;
import com.maurya.avenzo.repository.UserRepository;
import com.maurya.avenzo.security.CustomUserDetails;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class EventMemberService {
    private final EventMemberRepository eventMemberRepository;
    private final EventMemberMapper eventMemberMapper;
    private final UserRepository userRepository;
    private final EventRespository  eventRespository;

    @Transactional
    public List<EventMemberResponseDto> getEventMember(Long eventId) {
        // get the current user details
        // only owner of this event can view all members of this event
        CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserEntity user = Objects.requireNonNull(userDetails).getUserEntity();

        if(!eventMemberRepository.existsByEventIdAndUserIdAndRole(
                eventId,
                user.getId(),
                EventMemberRole.OWNER)) {
            throw new ApiException(ErrorCode.ACCESS_DENIED);
        }

        // now return all member of that event with their role

        List<EventMemberEntity> eventMemberEntities = eventMemberRepository.findByEventId(eventId);

        List<EventMemberResponseDto> eventMemberResponseDtoList = new ArrayList<>();

        for(EventMemberEntity eventMemberEntity : eventMemberEntities) {
            EventMemberResponseDto eventMemberResponseDto = eventMemberMapper.toEventMemberResponseDto(eventMemberEntity);
            eventMemberResponseDtoList.add(eventMemberResponseDto);
        }

        return eventMemberResponseDtoList;
    }

    @Transactional
    public EventMemberResponseDto addEventMember(Long eventId, Long userId) {
        // get the current user details
        // only owner of this event can view all members of this event
        CustomUserDetails userDetails = (CustomUserDetails) Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getPrincipal();
        UserEntity user = Objects.requireNonNull(userDetails).getUserEntity();

        if(!eventMemberRepository.existsByEventIdAndUserIdAndRole(
                eventId,
                user.getId(),
                EventMemberRole.OWNER)) {
            throw new ApiException(ErrorCode.ACCESS_DENIED);
        }

        // now add new as organizer or any other role
        // get the user from userId
        UserEntity userEntity = userRepository.findById(userId)
                .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND));

        // get event from eventId
        EventEntity eventEntity = eventRespository.findById(eventId)
                .orElseThrow(() -> new ApiException(ErrorCode.EVENT_NOT_FOUND));

        // check if event member already exists
        if(eventMemberRepository.existsByEventIdAndUserId(eventId, userId)) {
            throw new ApiException(ErrorCode.EVENT_MEMBER_ALREADY_EXISTS);
        }

        EventMemberEntity eventMemberEntity = new EventMemberEntity();
        eventMemberEntity.setUser(userEntity);
        eventMemberEntity.setEvent(eventEntity);
        eventMemberEntity.setRole(EventMemberRole.ORGANIZER);
        EventMemberEntity savedEventMemberEntity = eventMemberRepository.save(eventMemberEntity);

        return eventMemberMapper.toEventMemberResponseDto(savedEventMemberEntity);
    }

    @Transactional
    public EventMemberResponseDto deleteEventMember(Long eventId, Long userId) {
        // get the current user details
        // only owner of this event can view all members of this event
        CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserEntity user = Objects.requireNonNull(userDetails).getUserEntity();

        if(!eventMemberRepository.existsByEventIdAndUserIdAndRole(
                eventId,
                user.getId(),
                EventMemberRole.OWNER)) {
            throw new ApiException(ErrorCode.ACCESS_DENIED);
        }

        // check if user exist with userId
        UserEntity userEntity = userRepository.findById(userId)
                .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND));

        // requested userId and logged in user is same
        // then denied the request
        if(userEntity.getId().equals(user.getId())) {
            throw new ApiException(ErrorCode.ACCESS_DENIED);
        }
        // check if user exist with eventId
        EventEntity eventEntity = eventRespository.findById(eventId)
                .orElseThrow(() -> new ApiException(ErrorCode.EVENT_NOT_FOUND));

        // get EventMember and delete that
        EventMemberEntity eventMemberEntity = eventMemberRepository.findByEventIdAndUserId(eventId, userId)
                .orElseThrow(() -> new ApiException(ErrorCode.EVENT_MEMBER_NOT_FOUND));

        eventMemberRepository.delete(eventMemberEntity);

        return eventMemberMapper.toEventMemberResponseDto(eventMemberEntity);
    }

    @Transactional
    public EventMemberResponseDto updateEventMember(Long eventId, Long userId, EventMemberRequestDto eventMemberRequestDto) {
        // get the current user details
        // only owner of this event can view all members of this event
        CustomUserDetails userDetails = (CustomUserDetails) Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getPrincipal();
        UserEntity user = Objects.requireNonNull(userDetails).getUserEntity();

        if(!eventMemberRepository.existsByEventIdAndUserIdAndRole(
                eventId,
                user.getId(),
                EventMemberRole.OWNER)) {
            throw new ApiException(ErrorCode.ACCESS_DENIED);
        }

        // now add new as organizer or any other role
        // get the user from userId
        UserEntity userEntity = userRepository.findById(userId)
                .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND));

        // get event from eventId
        EventEntity eventEntity = eventRespository.findById(eventId)
                .orElseThrow(() -> new ApiException(ErrorCode.EVENT_NOT_FOUND));

        EventMemberEntity eventMemberEntity = eventMemberRepository.findByEventIdAndUserId(eventId, userId)
                .orElseThrow(() -> new ApiException(ErrorCode.EVENT_MEMBER_NOT_FOUND));

        // TODO:
        // check if current is owner then we cannot change its role, as owner is highest role

        // if current user is owner then cannot change back to volunteer
        if(eventMemberEntity.getRole().equals(EventMemberRole.OWNER) && userEntity.getId().equals(user.getId())) {
            throw new ApiException(ErrorCode.ACCESS_DENIED);
        }

        /*eventMemberEntity.setRole(EventMemberRole.ORGANIZER);*/
        eventMemberEntity.setRole(eventMemberRequestDto.getRole());

        EventMemberEntity savedEventMemberEntity = eventMemberRepository.save(eventMemberEntity);
        return eventMemberMapper.toEventMemberResponseDto(savedEventMemberEntity);
    }
}
