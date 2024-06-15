package ru.practicum.service.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.model.dto.event.*;
import ru.practicum.repository.EventRepository;

import java.util.List;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;

    @Override
    public EventFullDto updateEventLikeAdmin(UpdateEventAdminRequest eventRequestInput, long eventId) {
        return null;
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventFullDto> getAllEventInfoByParameters(List<Long> users, List<String> states, List<Long> categories, String rangeStart, String rangeEnd, Integer from, Integer size) {
        return null;
    }

    @Override
    public EventFullDto addEvent(long userId, NewEventDto eventDtoInput) {
        return null;
    }

    @Override
    public EventFullDto updateEventLikeUser(UpdateEventUserRequest eventDtoInput, long userId, long eventId) {
        return null;
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventShortDto> getEventsOfCurrentUser(long userId, Integer from, Integer size) {
        return null;
    }

    @Override
    @Transactional(readOnly = true)
    public EventFullDto getEventOfCurrentUserByEventId(long userId, long eventId) {
        return null;
    }

    @Override
    public EventFullDto getEventById(long id) {
        return null;
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventShortDto> getShortEventInfoByParameters(String text, List<Long> categories, Boolean paid, String rangeStart, String rangeEnd, Boolean onlyAvailable, String sort, Integer from, Integer size) {
        return null;
    }
}
