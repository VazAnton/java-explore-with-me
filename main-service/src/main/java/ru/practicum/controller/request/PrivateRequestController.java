package ru.practicum.controller.request;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.model.dto.request.ParticipationRequestDto;
import ru.practicum.service.request.RequestService;

import java.util.List;

@RestController
@RequestMapping("/users/{userId}/requests")
@RequiredArgsConstructor
public class PrivateRequestController {

    private final RequestService requestService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ParticipationRequestDto addRequestOnEvent(@PathVariable long userId,
                                                     @RequestParam long eventId) {
        return requestService.addRequestOnEvent(userId, eventId);
    }

    @PatchMapping("/{requestId}/cancel")
    public ParticipationRequestDto updateRequest(@PathVariable long userId,
                                                 @PathVariable long requestId) {
        return requestService.updateRequest(userId, requestId);
    }

    @GetMapping
    public List<ParticipationRequestDto> getRequestOfCurrentUserOnEventsOfStrangers(@PathVariable long userId) {
        return requestService.getRequestOfCurrentUserOnEventsOfStrangers(userId);
    }
}
