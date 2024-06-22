package ru.practicum.service.request;

import ru.practicum.dto.request.EventRequestStatusUpdateRequest;
import ru.practicum.dto.request.EventRequestStatusUpdateResult;
import ru.practicum.dto.request.ParticipationRequestDto;

import java.util.List;

public interface RequestService {

    List<ParticipationRequestDto> getRequestsOnEventsOfCurrentUser(long userId, long eventId);

    EventRequestStatusUpdateResult updateStatusOfEvents(EventRequestStatusUpdateRequest eventRequestStatusInput,
                                                        long userId, long eventId);

    ParticipationRequestDto addRequestOnEvent(long userId, long eventId);

    ParticipationRequestDto updateRequest(long userId, long requestId);

    List<ParticipationRequestDto> getRequestOfCurrentUserOnEventsOfStrangers(long userId);
}
