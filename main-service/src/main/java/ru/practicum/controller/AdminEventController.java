package ru.practicum.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.event.EventFullDto;
import ru.practicum.dto.event.UpdateEventAdminRequest;
import ru.practicum.service.event.EventService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/admin/events")
@RequiredArgsConstructor
public class AdminEventController {

    private final EventService eventService;

    @PatchMapping("/{eventId}")
    public EventFullDto updateEventLikeAdmin(@RequestBody @Valid UpdateEventAdminRequest eventRequestInput,
                                             @PathVariable long eventId) {
        return eventService.updateEventLikeAdmin(eventRequestInput, eventId);
    }

    @GetMapping
    public List<EventFullDto> getAllEventInfoByParameters(@RequestParam(required = false) List<Long> users,
                                                          @RequestParam(required = false) List<String> states,
                                                          @RequestParam(required = false) List<Long> categories,
                                                          @RequestParam(required = false) String rangeStart,
                                                          @RequestParam(required = false) String rangeEnd,
                                                          @RequestParam(defaultValue = "0") Integer from,
                                                          @RequestParam(defaultValue = "10") Integer size) {
        return eventService.getAllEventInfoByParameters(users, states, categories, rangeStart, rangeEnd, from, size);
    }
}
