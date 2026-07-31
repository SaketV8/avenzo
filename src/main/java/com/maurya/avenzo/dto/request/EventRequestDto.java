package com.maurya.avenzo.dto.request;

import com.maurya.avenzo.constant.EventStatus;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class EventRequestDto {
    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Description is required")
    private String description;

    @NotBlank(message = "Venue Detail is required")
    private String venue;

    @NotBlank(message = "City is required")
    private String city;

    @NotBlank(message = "Google Map link is required")
    private String mapLink;

    @NotNull
    @Future
    private LocalDateTime startTime;

    @NotNull
    @Future
    private LocalDateTime endTime;

    @NotNull
    private LocalDateTime registrationDeadline;

    @NotNull
    @Min(1)
    private Integer capacity;

    private String bannerImage;

    @NotNull
    private EventStatus status;

    @NotNull
    private Long categoryId;
}
