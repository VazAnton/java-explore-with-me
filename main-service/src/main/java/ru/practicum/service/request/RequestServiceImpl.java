package ru.practicum.service.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.exception.BadRequestException;
import ru.practicum.exception.EntityNotFoundException;
import ru.practicum.exception.IncorrectDataException;
import ru.practicum.mapper.AbstractEventMapper;
import ru.practicum.mapper.AbstractRequestMapper;
import ru.practicum.mapper.UserMapper;
import ru.practicum.model.dto.event.EventFullDto;
import ru.practicum.model.dto.request.EventRequestStatusUpdateRequest;
import ru.practicum.model.dto.request.EventRequestStatusUpdateResult;
import ru.practicum.model.dto.request.ParticipationRequestDto;
import ru.practicum.model.dto.user.UserDto;
import ru.practicum.model.enums.State;
import ru.practicum.model.enums.Status;
import ru.practicum.model.event.Event;
import ru.practicum.model.request.Request;
import ru.practicum.repository.EventRepository;
import ru.practicum.repository.RequestRepository;
import ru.practicum.service.event.EventService;
import ru.practicum.service.user.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {

    private final RequestRepository requestRepository;
    private final AbstractRequestMapper requestMapper;
    private final UserService userService;
    private final UserMapper userMapper;
    private final EventService eventService;
    private final EventRepository eventRepository;
    private final AbstractEventMapper eventMapper;

    @Override
    public ParticipationRequestDto addRequestOnEvent(long userId, long eventId) {
        UserDto userDto = userService.getUser(userId);
        Event eventFromDb = eventRepository.findById(eventId).orElseThrow(() ->
                new EntityNotFoundException("Внимание! События с таким уникальным номером не существует!"));
        if (!eventFromDb.getState().equals(State.PUBLISHED)) {
            log.error("Нельзя участвовать в неопубликованном событии!");
            throw new IncorrectDataException("Внимание! Нельзя участвовать в неопубликованном событии!");
        }
        EventFullDto eventFullDto = eventService.getEventById(eventId);
        if (eventFullDto.getInitiator().getId() == userId) {
            log.error("Инициатор события не может добавить запрос на участие в своём событии!");
            throw new IncorrectDataException("Внимание! Инициатор события не может добавить запрос на участие " +
                    "в своём событии!");
        }
        if (requestRepository.findByEventIdAndUserId(eventId, userId).isPresent()) {
            log.error("Нельзя добавить повторный запрос на участие в событии!");
            throw new IncorrectDataException("Внимание! Нельзя добавить повторный запрос на участие в событии!");
        }
        Request request = new Request();
        request.setCreated(LocalDateTime.now());
        request.setUser(userMapper.userDtoToUser(userDto));
        request.setStatus(Status.PENDING);
        if (eventFullDto.getParticipantLimit() == 0) {
            request.setStatus(Status.CONFIRMED);
            eventFullDto.setConfirmedRequests(eventFullDto.getConfirmedRequests() + 1);
        }
        int confirmedYet = requestRepository.findAllByEventIdAndStatusEquals(eventId, Status.CONFIRMED).size();
        if (!eventFullDto.getRequestModeration()) {
            if (eventFullDto.getParticipantLimit() != 0 && (confirmedYet + 1 > eventFullDto.getParticipantLimit())) {
                log.error("У этого события уже достигнут лимит запросов на участие!");
                throw new IncorrectDataException("Внимание! У этого события уже достигнут лимит запросов на участие!");
            }
            request.setStatus(Status.CONFIRMED);
            eventFullDto.setConfirmedRequests(eventFullDto.getConfirmedRequests() + 1);
        }
        Event event = eventMapper.eventFullDtoToEvent(eventFullDto);
        request.setEvent(event);
        log.info("Информация о событии успешно обновлена!");
        requestRepository.save(request);
        System.err.println(request);
        return requestMapper.requestToParticipationRequestDto(request);
    }

    @Override
    public ParticipationRequestDto updateRequest(long userId, long requestId) {
        userService.getUser(userId);
        Request requestFromDb = getRequestFromDb(requestId);
        requestFromDb.setStatus(Status.REJECTED);
        if (requestFromDb.getStatus().equals(Status.CONFIRMED)) {
            throw new IncorrectDataException("Внимание! Нельзя отменить принятый запрос на участие в событии!");
        }
        ParticipationRequestDto participationRequestDto = requestMapper.requestToParticipationRequestDto(requestFromDb);
        if (requestFromDb.getStatus().equals(Status.REJECTED)) {
            participationRequestDto.setStatus("CANCELED");
        }
        requestRepository.save(requestFromDb);
        log.info("Выбранный запрос на участие в событии успешно отменён!");
        return participationRequestDto;
    }

    private Request getRequestFromDb(long requestId) {
        return requestRepository.findById(requestId).orElseThrow(() ->
                new EntityNotFoundException("Внимание! События с таким уникальным номером не существует!"));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ParticipationRequestDto> getRequestOfCurrentUserOnEventsOfStrangers(long userId) {
        userService.getUser(userId);
        return requestRepository.findAllByUserId(userId).stream()
                .map(requestMapper::requestToParticipationRequestDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ParticipationRequestDto> getRequestsOnEventsOfCurrentUser(long userId, long eventId) {
        userService.getUser(userId);
        eventService.getEventById(eventId);
        return requestRepository.findAllByEventId(eventId).stream()
                .map(requestMapper::requestToParticipationRequestDto)
                .collect(Collectors.toList());
    }

    @Override
    public EventRequestStatusUpdateResult updateStatusOfEvents(EventRequestStatusUpdateRequest eventRequestStatusInput,
                                                               long userId, long eventId) {
        userService.getUser(userId);
        EventFullDto eventFullDto = eventService.getEventById(eventId);
        EventRequestStatusUpdateResult result = new EventRequestStatusUpdateResult();
        List<Request> requests = requestRepository.findAllByIdIn(eventRequestStatusInput.getRequestIds());
        List<ParticipationRequestDto> confirmedRequests = new ArrayList<>();
        List<ParticipationRequestDto> rejectedRequests = new ArrayList<>();
        if (requests.stream()
                .anyMatch(request -> !request.getStatus().equals(Status.PENDING))) {
            log.error("Статус можно изменить только у заявок, находящихся в состоянии ожидания!");
            throw new IncorrectDataException("Внимание! Статус можно изменить только у заявок, находящихся в " +
                    "состоянии ожидания!");
        }
        switch (eventRequestStatusInput.getStatus()) {
            case "CONFIRMED":
                int confirmedYet = requestRepository.findAllByEventIdAndStatusEquals(eventId, Status.CONFIRMED).size();
                for (Request request : requests) {
                    int confirmed = request.getEvent().getConfirmedRequests();
                    if (!(confirmedYet + 1 > request.getEvent().getParticipantLimit())
                            && request.getEvent().getParticipantLimit() != 0) {
                        request.setStatus(Status.CONFIRMED);
                        ParticipationRequestDto confirmedRequest = requestMapper.requestToParticipationRequestDto(request);
                        confirmedRequests.add(confirmedRequest);
                        confirmed = confirmed + 1;
                        eventFullDto.setConfirmedRequests(confirmed);
                    } else {
                        request.setStatus(Status.REJECTED);
                        ParticipationRequestDto rejectedRequest = requestMapper.requestToParticipationRequestDto(request);
                        rejectedRequests.add(rejectedRequest);
                        throw new IncorrectDataException("Внимание! У этого события уже достигнут лимит запросов на " +
                                "участие!");
                    }
                }
                break;
            case "REJECTED":
                if (requests.stream()
                        .map(requestMapper::requestToParticipationRequestDto)
                        .anyMatch(confirmedRequests::contains)) {
                    log.error("Нельзя отменить уже принятую заявку на участие в событии!");
                    throw new IncorrectDataException("Внимание! Нельзя отменить уже принятую заявку на участие в " +
                            "событии!");
                }
                rejectedRequests.addAll(requests.stream()
                        .peek(request -> request.setStatus(Status.REJECTED))
                        .map(requestMapper::requestToParticipationRequestDto)
                        .collect(Collectors.toList()));
                break;
            default:
                log.error("Передан неверный статус события!");
                throw new BadRequestException("Внимание! Передан неверный статус события!");
        }
        result.setConfirmedRequests(confirmedRequests);
        result.setRejectedRequests(rejectedRequests);
        requestRepository.saveAll(requests);
        return result;
    }
}
