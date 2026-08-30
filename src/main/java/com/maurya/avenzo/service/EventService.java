package com.maurya.avenzo.service;

import com.maurya.avenzo.constant.EventMemberRole;
import com.maurya.avenzo.constant.EventStatus;
import com.maurya.avenzo.dto.request.EventRequestDto;
import com.maurya.avenzo.dto.response.EventResponseDto;
import com.maurya.avenzo.entity.CategoryEntity;
import com.maurya.avenzo.entity.EventEntity;
import com.maurya.avenzo.entity.EventMemberEntity;
import com.maurya.avenzo.entity.UserEntity;
import com.maurya.avenzo.exception.ApiException;
import com.maurya.avenzo.exception.ErrorCode;
import com.maurya.avenzo.mapper.EventMapper;
import com.maurya.avenzo.repository.CategoryRepository;
import com.maurya.avenzo.repository.EventMemberRepository;
import com.maurya.avenzo.repository.EventRespository;
import com.maurya.avenzo.security.CustomUserDetails;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Service
@RequiredArgsConstructor
@Slf4j
public class EventService {

    private final CategoryRepository categoryRepository;
    private final EventRespository eventRepository;
    private final EventMemberRepository eventMemberRepository;
    private final EventMapper eventMapper;

    @Transactional
    public EventResponseDto createEvent(EventRequestDto eventRequestDto) {
        //get the current user details
        CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        UserEntity user = userDetails.getUserEntity();

        log.info("🐸 Email: {}", user.getEmail());

        // Fetch category
        CategoryEntity category = categoryRepository.findById(eventRequestDto.getCategoryId())
                .orElseThrow(() -> new ApiException(ErrorCode.CATEGORY_DOES_NOT_EXIST));


        // now we can create the event with current email as the OWNER of that event
        EventEntity event = new EventEntity();
        event.setTitle(eventRequestDto.getTitle());
        event.setDescription(eventRequestDto.getDescription());
        event.setVenue(eventRequestDto.getVenue());
        event.setCity(eventRequestDto.getCity());
        event.setMapLink(eventRequestDto.getMapLink());
        event.setStartTime(eventRequestDto.getStartTime());
        event.setEndTime(eventRequestDto.getEndTime());
        event.setRegistrationDeadline(eventRequestDto.getRegistrationDeadline());
        event.setCapacity(eventRequestDto.getCapacity());

        // when event is created then capacity == available seat
        event.setAvailableSeats(event.getCapacity());

        event.setBannerImage(eventRequestDto.getBannerImage());
        event.setStatus(eventRequestDto.getStatus());
        event.setCategory(category);

        EventEntity savedEvent = eventRepository.save(event);

        // current user a owner for this event
        EventMemberEntity eventMemberEntity = new EventMemberEntity();
        eventMemberEntity.setEvent(savedEvent);
        eventMemberEntity.setUser(user);
        eventMemberEntity.setRole(EventMemberRole.OWNER);

        eventMemberRepository.save(eventMemberEntity);

        /*
        return new EventResponseDto(
                savedEvent.getId(),
                savedEvent.getTitle(),
                savedEvent.getDescription(),
                savedEvent.getVenue(),
                savedEvent.getCity(),
                savedEvent.getMapLink(),
                savedEvent.getStartTime(),
                savedEvent.getEndTime(),
                savedEvent.getRegistrationDeadline(),
                savedEvent.getCapacity(),
                savedEvent.getAvailableSeats(),
                savedEvent.getBannerImage(),
                savedEvent.getStatus(),
                savedEvent.getCategory().getId(),
                savedEvent.getCategory().getName(),
                savedEvent.getUpdatedAt(),
                savedEvent.getCreatedAt()
        );
        */
        return eventMapper.toEventResponseDto(savedEvent);
    }

    public List<EventResponseDto> getAllEvents() {
//        List<EventEntity> events = eventRepository.findAll();
        List<EventEntity> events = eventRepository.findAllByStatus(EventStatus.PUBLISHED);
        List<EventResponseDto> eventResponseDtoList = new ArrayList<>();

        for (EventEntity event : events) {
            /*
            EventResponseDto eventResponseDto = new EventResponseDto(
                    event.getId(),
                    event.getTitle(),
                    event.getDescription(),
                    event.getVenue(),
                    event.getCity(),
                    event.getMapLink(),
                    event.getStartTime(),
                    event.getEndTime(),
                    event.getRegistrationDeadline(),
                    event.getCapacity(),
                    event.getAvailableSeats(),
                    event.getBannerImage(),
                    event.getStatus(),
                    event.getCategory().getId(),
                    event.getCategory().getName(),
                    event.getUpdatedAt(),
                    event.getCreatedAt()
            );
            */

            EventResponseDto eventResponseDto = eventMapper.toEventResponseDto(event);
            eventResponseDtoList.add(eventResponseDto);
        }

        return eventResponseDtoList;
    }

    public EventResponseDto getEvents(Long eventId) {
        EventEntity eventEntity = eventRepository.findById(eventId).orElseThrow(() -> {
            return new ApiException(ErrorCode.EVENT_NOT_FOUND);
        });

        EventResponseDto eventResponseDto = eventMapper.toEventResponseDto(eventEntity);
        return eventResponseDto;
    }

    public EventResponseDto updateEvent(Long eventId, @Valid EventRequestDto eventRequestDto) {
        //get the current user details
        CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserEntity user = userDetails.getUserEntity();

        // check if this event owned by current user
        // Authorization
        if (!eventMemberRepository.existsByEventIdAndUserIdAndRole(
                eventId,
                user.getId(),
                EventMemberRole.OWNER)) {

            throw new ApiException(ErrorCode.ACCESS_DENIED);
        }
        // then apply the update
        // get the event
        EventEntity event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ApiException(ErrorCode.EVENT_NOT_FOUND));

        // check for category :)
        CategoryEntity category = categoryRepository.findById(eventRequestDto.getCategoryId())
                .orElseThrow(() -> new ApiException(ErrorCode.CATEGORY_DOES_NOT_EXIST));

        // avoiding reducing the seats if there are already registered users
        int registeredSeats = event.getCapacity() - event.getAvailableSeats();

        if (eventRequestDto.getCapacity() < registeredSeats) {
            throw new ApiException(ErrorCode.CAPACITY_CANNOT_BE_REDUCED);
        }

        // now updating the value in the event
        event.setTitle(eventRequestDto.getTitle());
        event.setDescription(eventRequestDto.getDescription());
        event.setVenue(eventRequestDto.getVenue());
        event.setCity(eventRequestDto.getCity());
        event.setMapLink(eventRequestDto.getMapLink());
        event.setStartTime(eventRequestDto.getStartTime());
        event.setEndTime(eventRequestDto.getEndTime());
        event.setRegistrationDeadline(eventRequestDto.getRegistrationDeadline());

        event.setCapacity(eventRequestDto.getCapacity());

        // when event is created then capacity == available seat
        event.setAvailableSeats(event.getCapacity() - registeredSeats);

        event.setBannerImage(eventRequestDto.getBannerImage());
        event.setStatus(eventRequestDto.getStatus());
        event.setCategory(category);

        EventEntity updatedEvent = eventRepository.save(event);

        return  eventMapper.toEventResponseDto(updatedEvent);
    }

    public EventResponseDto deleteEvent(Long eventId) {
        //get the current user details
        CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserEntity user = userDetails.getUserEntity();

        // check if this event owned by current user
        // Authorization
        if (!eventMemberRepository.existsByEventIdAndUserIdAndRole(
                eventId,
                user.getId(),
                EventMemberRole.OWNER)) {

            throw new ApiException(ErrorCode.ACCESS_DENIED);
        }

        EventEntity event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ApiException(ErrorCode.EVENT_NOT_FOUND));

        // not doing hard delete from the table, just changes STATUS of the event
        event.setStatus(EventStatus.CANCELLED);

        EventEntity updatedEvent = eventRepository.save(event);

        /*eventRepository.delete(event);*/
        return eventMapper.toEventResponseDto(updatedEvent);
    }

    public List<EventResponseDto> getAllEventByOwner() {
        //get the current user details
        CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserEntity user = userDetails.getUserEntity();

        List<EventMemberEntity> eventMemberEntityList = eventMemberRepository.findAllByUserIdAndRole(user.getId(), EventMemberRole.OWNER);

        List<EventResponseDto> eventResponseDtoList = new ArrayList<>();
        for (EventMemberEntity eventMemberEntity : eventMemberEntityList) {
            eventResponseDtoList.add(
                    eventMapper.toEventResponseDto(
                            eventMemberEntity.getEvent()
                    )
            );
        }

        return eventResponseDtoList;
    }
}
