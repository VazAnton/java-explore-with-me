package ru.practicum.service.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.model.dto.request.EventRequestStatusUpdateRequest;
import ru.practicum.model.dto.request.EventRequestStatusUpdateResult;
import ru.practicum.model.dto.request.ParticipationRequestDto;
import ru.practicum.repository.RequestRepository;

import java.util.List;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {

    private final RequestRepository requestRepository;

    @Override
    @Transactional(readOnly = true)
    public List<ParticipationRequestDto> getRequestsOnEventsOfCurrentUser(long userId, long eventId) {
        return null;
    }

    @Override
    @Transactional(readOnly = true)
    public EventRequestStatusUpdateResult updateStatusOfEvents(EventRequestStatusUpdateRequest eventRequestStatusInput, long userId, long eventId) {
        return null;
    }

    @Override
    public ParticipationRequestDto addRequestOnEvent(long userId, long eventId) {
        return null;
    }

    @Override
    public ParticipationRequestDto updateRequest(long userId, long requestId) {
        return null;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ParticipationRequestDto> getRequestOfCurrentUserOnEventsOfStrangers(long userId) {
        return null;
    }
}
