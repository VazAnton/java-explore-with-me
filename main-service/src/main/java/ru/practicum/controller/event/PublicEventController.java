package ru.practicum.controller.event;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.exception.EntityNotFoundException;
import ru.practicum.model.dto.event.EventFullDto;
import ru.practicum.model.dto.event.EventShortDto;
import ru.practicum.model.enums.State;
import ru.practicum.service.event.EventService;

import java.util.List;

@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
public class PublicEventController {

    private final EventService eventService;

    @GetMapping("/{id}")
    public EventFullDto getEventById(@PathVariable long id) {
        EventFullDto eventFullDto = eventService.getEventById(id);
        if (!eventFullDto.getState().equals(State.PUBLISHED)) {
            throw new EntityNotFoundException("Внимание! События с таким уникальным номером не существует!");
        }
        return eventService.getEventById(id);
    }

    @GetMapping
    public List<EventShortDto> getShortEventInfoByParameters(@RequestParam(required = false) String text,
                                                             @RequestParam(required = false) List<Long> categories,
                                                             @RequestParam(required = false) Boolean paid,
                                                             @RequestParam(required = false) String rangeStart,
                                                             @RequestParam(required = false) String rangeEnd,
                                                             @RequestParam(defaultValue = "false") Boolean onlyAvailable,
                                                             @RequestParam(required = false) String sort,
                                                             @RequestParam(defaultValue = "0") Integer from,
                                                             @RequestParam(defaultValue = "10") Integer size) {
        return eventService.getShortEventInfoByParameters(text, categories, paid, rangeStart, rangeEnd, onlyAvailable,
                sort, from, size);
    }
}
