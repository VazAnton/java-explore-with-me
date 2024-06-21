package ru.practicum.service.event;

import ru.practicum.dto.event.*;

import java.util.List;

public interface EventService {

    EventFullDto updateEventLikeAdmin(UpdateEventAdminRequest eventRequestInput, long eventId);

    List<EventFullDto> getAllEventInfoByParameters(List<Long> users, List<String> states, List<Long> categories,
                                                   String rangeStart, String rangeEnd, Integer from, Integer size);

    EventFullDto addEvent(long userId, EventDtoInput eventDtoInput);

    EventFullDto updateEventLikeUser(UpdateEventUserRequest eventDtoInput, long userId, long eventId);

    List<EventShortDto> getEventsOfCurrentUser(long userId, Integer from, Integer size);

    EventFullDto getEventOfCurrentUserByEventId(long userId, long eventId);

    EventFullDto getEventById(long id);

    List<EventShortDto> getShortEventInfoByParameters(String text, List<Long> categories, Boolean paid,
                                                      String rangeStart, String rangeEnd, Boolean onlyAvailable,
                                                      String sort, Integer from, Integer size);
}
