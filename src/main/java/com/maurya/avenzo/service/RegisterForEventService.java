package com.maurya.avenzo.service;

import com.maurya.avenzo.constant.EventMemberRole;
import com.maurya.avenzo.constant.RegistrationStatus;
import com.maurya.avenzo.dto.request.EventCheckInRequestDto;
import com.maurya.avenzo.dto.response.EventDashboardResponseDto;
import com.maurya.avenzo.dto.response.EventResponseDto;
import com.maurya.avenzo.dto.response.RegisterForEventResponseDto;
import com.maurya.avenzo.entity.EventEntity;
import com.maurya.avenzo.entity.EventMemberEntity;
import com.maurya.avenzo.entity.RegistrationEntity;
import com.maurya.avenzo.entity.UserEntity;
import com.maurya.avenzo.exception.ApiException;
import com.maurya.avenzo.exception.ErrorCode;
import com.maurya.avenzo.mapper.EventMapper;
import com.maurya.avenzo.mapper.RegistorForEventMapper;
import com.maurya.avenzo.repository.EventMemberRepository;
import com.maurya.avenzo.repository.EventRespository;
import com.maurya.avenzo.repository.RegisterForEventRepository;
import com.maurya.avenzo.repository.UserRepository;
import com.maurya.avenzo.security.CustomUserDetails;
//import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RegisterForEventService {

    private final RegisterForEventRepository registerForEventRepository;
    private final UserRepository userRepository;
    private final EventRespository eventRespository;

    private final RegistorForEventMapper registorForEventMapper;
    private final EventMemberRepository eventMemberRepository;

    private final EventMapper eventMapper;

    @Transactional
    public RegisterForEventResponseDto registerForEvent(Long eventId) {
        //get the current user details
        CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserEntity user = userDetails.getUserEntity();

        if (registerForEventRepository.existsByUserIdAndEventId(user.getId(), eventId)) {
            throw new ApiException(ErrorCode.ALREADY_REGISTERED);
        }

        // get the event
        EventEntity event = eventRespository.findById(eventId)
                .orElseThrow(() -> new ApiException(ErrorCode.EVENT_NOT_FOUND));

        // default
        RegistrationStatus REG_STATUS = RegistrationStatus.REGISTERED;

        // check if seat available then waitinglist
        if (event.getAvailableSeats() <= 0) {
            REG_STATUS = RegistrationStatus.WAITLISTED;
        }

        // do the registration
        RegistrationEntity registrationEntity = new RegistrationEntity();
        registrationEntity.setUser(user);
        registrationEntity.setEvent(event);
        registrationEntity.setStatus(REG_STATUS);

        // GENERATING RANDOM TICKET NUMBER
        String ticketNumber = "AVENZO-" +
                UUID.randomUUID()
                        .toString()
                        .replace("-", "")
                        .substring(0, 12)
                        .toUpperCase();

        registrationEntity.setTicketNumber(ticketNumber);

        RegistrationEntity savedRegistrationEntity = registerForEventRepository.save(registrationEntity);

        // now reduce seat by 1
        event.setAvailableSeats(event.getAvailableSeats() - 1);
        eventRespository.save(event);

        return registorForEventMapper.toRegisterForEventDto(savedRegistrationEntity);
    }

    @Transactional
    public List<EventResponseDto> getUserRegisteredEvents() {
        //get the current user details
        CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserEntity user = userDetails.getUserEntity();


        List<RegistrationEntity> registrationEntities = registerForEventRepository.findByUserId(user.getId());
        List<EventResponseDto> eventResponseDtoList = new ArrayList<>();

        for (RegistrationEntity registrationEntity : registrationEntities) {
            EventEntity eventEntity = registrationEntity.getEvent();
            EventResponseDto eventResponseDto = eventMapper.toEventResponseDto(eventEntity);
            eventResponseDtoList.add(eventResponseDto);
        }

        return eventResponseDtoList;
    }

    @Transactional(readOnly = true)
    public RegisterForEventResponseDto getMyRegistrationForEvent(Long eventId) {
        CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserEntity user = userDetails.getUserEntity();

        RegistrationEntity registrationEntity = registerForEventRepository.findByUserIdAndEventId(user.getId(), eventId)
                .orElseThrow(() -> new ApiException(ErrorCode.REGISTRATION_NOT_FOUND));

        return registorForEventMapper.toRegisterForEventDto(registrationEntity);
    }

    @Transactional
    public RegisterForEventResponseDto deregisterForEvent(Long eventId) {
        //get the current user details
        CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserEntity user = userDetails.getUserEntity();

        // check if current user is registered for current event
        RegistrationEntity registrationEntity = registerForEventRepository.findByUserIdAndEventId(user.getId(), eventId)
                .orElseThrow(() -> new ApiException(ErrorCode.REGISTRATION_NOT_FOUND));

        // delete the registration for the current user
        registerForEventRepository.delete(registrationEntity);

        // now increase available seats
        EventEntity event = registrationEntity.getEvent();

        event.setAvailableSeats(event.getAvailableSeats() + 1);
        eventRespository.save(event);

        // we return that registration, which we have deleted
        return registorForEventMapper.toRegisterForEventDto(registrationEntity);
    }

    @Transactional
    public List<RegisterForEventResponseDto> getEventRegistrations(Long eventId) {
        System.out.println("🐸🐸 get registration list");
        // only owner of this event can see the list or admin
        CustomUserDetails userDetails = (CustomUserDetails) Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getPrincipal();
        UserEntity user = Objects.requireNonNull(userDetails).getUserEntity();

        // check if this user has owner role
        if (!eventMemberRepository.existsByEventIdAndUserIdAndRole(
                eventId,
                user.getId(),
                EventMemberRole.OWNER)) {
            throw new ApiException(ErrorCode.ACCESS_DENIED);
        }

        // NOW return all registered users
        List<RegistrationEntity> registrationEntities = registerForEventRepository.findByEventId(eventId);
        List<RegisterForEventResponseDto> registerForEventResponseDtoList = new ArrayList<>();

        for (RegistrationEntity registrationEntity : registrationEntities) {
            RegisterForEventResponseDto registerForEventResponseDto = registorForEventMapper.toRegisterForEventDto(registrationEntity);
            registerForEventResponseDtoList.add(registerForEventResponseDto);
        }

        return registerForEventResponseDtoList;
    }

    @Transactional
    public RegisterForEventResponseDto checkInForEvent(Long eventId, EventCheckInRequestDto eventCheckInRequestDto) {
        System.out.println("🍃🍃 Checkin Service Started");
        //get the current user details
        CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserEntity user = userDetails.getUserEntity();

        // check role for current owner
        if (!eventMemberRepository.existsByEventIdAndUserIdAndRoleIn(
                eventId,
                user.getId(),
                List.of(EventMemberRole.OWNER,
                        EventMemberRole.ORGANIZER,
                        EventMemberRole.VOLUNTEER
                ))) {
            throw new ApiException(ErrorCode.ACCESS_DENIED);
        }
        /*if(!eventMemberRepository.existsByEventIdAndUserIdAndRole(
                eventId,
                user.getId(),
                EventMemberRole.OWNER
        )) {
            throw new ApiException(ErrorCode.ACCESS_DENIED);
        }*/

        System.out.println("🍃🍃 Verification Done in checkin");

        // get the event
        EventEntity event = eventRespository.findById(eventId)
                .orElseThrow(() -> new ApiException(ErrorCode.EVENT_NOT_FOUND));

        // now get the Registration by the ticker number from request body
        RegistrationEntity registrationEntity = registerForEventRepository.findByTicketNumber(eventCheckInRequestDto.getTicketNumber())
                .orElseThrow(() -> new ApiException(ErrorCode.REGISTRATION_NOT_FOUND));

        // check if already attended
        if (registrationEntity.getStatus() == RegistrationStatus.ATTENDED) {
            throw new ApiException(ErrorCode.ALREADY_ATTENDED_EVENT);
        }

        registrationEntity.setStatus(RegistrationStatus.ATTENDED);

        registrationEntity.setUpdatedAt(LocalDateTime.now());

        RegistrationEntity savedRegistrationEntity = registerForEventRepository.save(registrationEntity);

        return registorForEventMapper.toRegisterForEventDto(savedRegistrationEntity);
    }

    @Transactional
    public List<RegisterForEventResponseDto> attendeesForEvent(Long eventId) {
        //get the current user details
        CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserEntity user = userDetails.getUserEntity();

        // check if current user is owner
        if (!eventMemberRepository.existsByEventIdAndUserIdAndRole(
                eventId,
                user.getId(),
                EventMemberRole.OWNER
        )) {
            throw new ApiException(ErrorCode.ACCESS_DENIED);
        }

        // now find all the registration with Status ATTENDED
        List<RegistrationEntity> registrationEntities = registerForEventRepository.findByEventIdAndStatus(eventId, RegistrationStatus.ATTENDED);
        List<RegisterForEventResponseDto> registerForEventResponseDtoList = new ArrayList<>();

        for (RegistrationEntity registrationEntity : registrationEntities) {
            RegisterForEventResponseDto registerForEventResponseDto = registorForEventMapper.toRegisterForEventDto(registrationEntity);
            registerForEventResponseDtoList.add(registerForEventResponseDto);
        }

        return registerForEventResponseDtoList;
    }

    @Transactional
    public EventDashboardResponseDto dashboardForEvent(Long eventId) {
        //get the current user details
        CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserEntity user = userDetails.getUserEntity();

        // check if current user is owner
        if (!eventMemberRepository.existsByEventIdAndUserIdAndRole(
                eventId,
                user.getId(),
                EventMemberRole.OWNER
        )) {
            throw new ApiException(ErrorCode.ACCESS_DENIED);
        }

        // get the event and then extract capacity and available seats
        EventEntity eventEntity = eventRespository.findById(eventId)
                .orElseThrow(() -> new ApiException(ErrorCode.EVENT_NOT_FOUND));

        Long Capacity = Long.valueOf(eventEntity.getCapacity());
        Long AvailableSeats = Long.valueOf(eventEntity.getAvailableSeats());

        Long Registered = registerForEventRepository.countByEventIdAndStatus(eventId, RegistrationStatus.REGISTERED);
        Long Attendees = registerForEventRepository.countByEventIdAndStatus(eventId, RegistrationStatus.ATTENDED);

        Long Organizers = eventMemberRepository.countByEventIdAndRole(eventId, EventMemberRole.ORGANIZER);

        Long Volunteers = eventMemberRepository.countByEventIdAndRole(eventId, EventMemberRole.VOLUNTEER);

        return new EventDashboardResponseDto(
                Capacity,
                AvailableSeats,

                Registered,
                Attendees,

                Organizers,
                Volunteers
        );
    }
}
