package ru.practicum.controller.event;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.model.dto.event.EventFullDto;
import ru.practicum.model.dto.event.EventShortDto;
import ru.practicum.model.dto.event.NewEventDto;
import ru.practicum.model.dto.event.UpdateEventUserRequest;
import ru.practicum.model.dto.request.EventRequestStatusUpdateRequest;
import ru.practicum.model.dto.request.EventRequestStatusUpdateResult;
import ru.practicum.model.dto.request.ParticipationRequestDto;
import ru.practicum.service.event.EventService;
import ru.practicum.service.request.RequestService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/users/{userId}/events")
@RequiredArgsConstructor
public class PrivateEventController {

    private final EventService eventService;
    private final RequestService requestService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto addEvent(@PathVariable long userId,
                                 @Valid @RequestBody NewEventDto eventDtoInput) {
        return eventService.addEvent(userId, eventDtoInput);
    }

    @PatchMapping("/{eventId}")
    public EventFullDto updateEventLikeUser(@RequestBody UpdateEventUserRequest eventDtoInput,
                                            @PathVariable long userId,
                                            @PathVariable long eventId) {
        return eventService.updateEventLikeUser(eventDtoInput, userId, eventId);
    }

    @GetMapping
    public List<EventShortDto> getEventsOfCurrentUser(@PathVariable long userId,
                                                     @RequestParam(defaultValue = "0") Integer from,
                                                     @RequestParam(defaultValue = "10") Integer size) {
        return eventService.getEventsOfCurrentUser(userId, from, size);
    }

    @GetMapping("/{eventId}")
    public EventFullDto getEventOfCurrentUserByEventId(@PathVariable long userId,
                                                       @PathVariable long eventId) {
        return eventService.getEventOfCurrentUserByEventId(userId, eventId);
    }

    @GetMapping("/{eventId}/requests")
    public List<ParticipationRequestDto> getRequestsOnEventsOfCurrentUser(@PathVariable long userId,
                                                                          @PathVariable long eventId) {
        return requestService.getRequestsOnEventsOfCurrentUser(userId, eventId);
    }

    @PatchMapping("/{eventId}/requests")
    public EventRequestStatusUpdateResult updateStatusOfEvents(@RequestBody @Valid EventRequestStatusUpdateRequest
                                                                       eventRequestStatusInput,
                                                               @PathVariable long userId,
                                                               @PathVariable long eventId) {
        return requestService.updateStatusOfEvents(eventRequestStatusInput, userId, eventId);
    }
}
