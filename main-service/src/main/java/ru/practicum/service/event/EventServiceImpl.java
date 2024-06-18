package ru.practicum.service.event;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.StatisticsClient;
import ru.practicum.StatisticsModelDtoInput;
import ru.practicum.StatisticsModelDtoOutput;
import ru.practicum.exception.BadRequestException;
import ru.practicum.exception.EntityNotFoundException;
import ru.practicum.exception.IncorrectDataException;
import ru.practicum.mapper.AbstractEventMapper;
import ru.practicum.mapper.CategoryMapper;
import ru.practicum.mapper.LocationMapper;
import ru.practicum.mapper.UserMapper;
import ru.practicum.model.category.Category;
import ru.practicum.model.dto.category.CategoryDto;
import ru.practicum.model.dto.event.*;
import ru.practicum.model.dto.user.UserDto;
import ru.practicum.model.enums.State;
import ru.practicum.model.enums.Status;
import ru.practicum.model.event.Event;
import ru.practicum.model.location.Location;
import ru.practicum.model.user.User;
import ru.practicum.repository.EventRepository;
import ru.practicum.repository.LocationRepository;
import ru.practicum.repository.RequestRepository;
import ru.practicum.repository.UserRepository;
import ru.practicum.service.category.CategoryService;
import ru.practicum.service.user.UserService;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final AbstractEventMapper eventMapper;
    private final CategoryService categoryService;
    private final CategoryMapper categoryMapper;
    private final StatisticsClient statisticsClient;
    private final UserService userService;
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final LocationRepository locationRepository;
    private final LocationMapper locationMapper;
    private final RequestRepository requestRepository;
    private final HttpServletRequest httpServletRequest;
    private final ObjectMapper objectMapper;
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");


    @Override
    public EventFullDto addEvent(long userId, NewEventDto eventDtoInput) {
        Event event = eventMapper.newEventDtoToEvent(eventDtoInput);
        CategoryDto categoryDto = categoryService.getCategory(eventDtoInput.getCategory());
        Category category = categoryMapper.categoryDtoToCategory(categoryDto);
        event.setCategory(category);
        event.setConfirmedRequests(0);
        event.setCreatedOn(LocalDateTime.now());
        if (eventDtoInput.getPaid() == null) {
            event.setPaid(false);
        }
        if (eventDtoInput.getRequestModeration() == null) {
            event.setRequestModeration(true);
        }
        LocalDateTime eventDate = LocalDateTime.parse(eventDtoInput.getEventDate(), DATE_TIME_FORMATTER);
        if (eventDate.isBefore(LocalDateTime.now().plusHours(2))) {
            log.error("Дата и время, на которые намечено событие не может быть раньше, чем через два часа от текущего " +
                    "момента!");
            throw new BadRequestException("Внимание! Дата и время, на которые намечено событие не может быть раньше, " +
                    "чем через два часа от текущего момента!");
        }
        if (eventDtoInput.getParticipantLimit() != null) {
            if (eventDtoInput.getParticipantLimit() < 0) {
                log.error("Лимит участников не может быть отрицательным!");
                throw new BadRequestException("Внимание! Лимит участников не может быть отрицательным!");
            }
            event.setParticipantLimit(eventDtoInput.getParticipantLimit());
        } else {
            event.setParticipantLimit(0);
        }
        UserDto userDto = userService.getUser(userId);
        User userFromDb = userMapper.userDtoToUser(userDto);
        event.setInitiator(userFromDb);
        Location location = locationMapper.locationDtoToLocation(eventDtoInput.getLocation());
        locationRepository.save(location);
        event.setLocation(location);
        event.setPublishedOn(LocalDateTime.now());
        event.setState(State.PENDING);
        event.setViews(0);
        eventRepository.save(event);
        log.info("Информация о новом событии успешно сохранена!");
        return eventMapper.eventToEventFullDto(event);
    }

    private void validateEventInTimeToUpdateLikeAdmin(Event eventFromDb, UpdateEventAdminRequest eventRequestInput) {
        if (eventRequestInput.getAnnotation() != null) {
            eventFromDb.setAnnotation(eventRequestInput.getAnnotation());
        }
        if (eventRequestInput.getCategory() != null) {
            CategoryDto categoryDto = categoryService.getCategory(eventRequestInput.getCategory());
            Category category = categoryMapper.categoryDtoToCategory(categoryDto);
            eventFromDb.setCategory(category);
        }
        if (eventRequestInput.getDescription() != null) {
            eventFromDb.setDescription(eventRequestInput.getDescription());
        }
        if (eventRequestInput.getEventDate() != null) {
            LocalDateTime eventDate = LocalDateTime.parse(eventRequestInput.getEventDate(), DATE_TIME_FORMATTER);
            if (eventDate.isBefore(eventFromDb.getPublishedOn())) {
                log.error("Дата начала изменяемого события должна быть не ранее чем за час от даты публикации!");
                throw new BadRequestException("Внимание! Дата начала изменяемого события должна быть не ранее " +
                        "чем за час от даты публикации!");
            }
            eventFromDb.setEventDate(eventDate);
        }
        if (eventRequestInput.getLocation() != null) {
            Location location = locationMapper.locationDtoToLocation(eventRequestInput.getLocation());
            locationRepository.save(location);
            eventFromDb.setLocation(location);
        }
        if (eventRequestInput.getPaid() != null) {
            eventFromDb.setPaid(eventRequestInput.getPaid());
        }
        if (eventRequestInput.getParticipantLimit() != null) {
            if (eventRequestInput.getParticipantLimit() < 0) {
                log.error("Лимит участников не может быть отрицательным!");
                throw new BadRequestException("Внимание! Лимит участников не может быть отрицательным!");
            }
            eventFromDb.setParticipantLimit(eventRequestInput.getParticipantLimit());
        }
        if (eventRequestInput.getRequestModeration() != null) {
            eventFromDb.setRequestModeration(eventRequestInput.getRequestModeration());
        }
        if (eventRequestInput.getStateAction() != null) {
            switch (eventRequestInput.getStateAction()) {
                case "PUBLISH_EVENT":
                    if (!eventFromDb.getState().equals(State.PENDING)) {
                        log.error("Можно опубликовать только события, находящиеся в ожидании!");
                        throw new IncorrectDataException("Внимание! Можно опубликовать только события, находящиеся " +
                                "в ожидании!");
                    }
                    eventFromDb.setState(State.PUBLISHED);
                    break;
                case "REJECT_EVENT":
                    if (eventFromDb.getState().equals(State.PUBLISHED)) {
                        log.error("Можно отменить только неопубликованные события!");
                        throw new IncorrectDataException("Внимание! Можно отменить только неопубликованные события!");
                    }
                    eventFromDb.setState(State.CANCELED);
                    break;
                default:
                    log.error("Передан неверный статус события!");
                    throw new BadRequestException("Внимание! Передан неверный статус события!");
            }
        }
        if (eventRequestInput.getTitle() != null) {
            eventFromDb.setTitle(eventRequestInput.getTitle());
        }
    }

    @Override
    public EventFullDto updateEventLikeAdmin(UpdateEventAdminRequest eventRequestInput, long eventId) {
        Event eventFromDb = getEventFromDb(eventId);
        validateEventInTimeToUpdateLikeAdmin(eventFromDb, eventRequestInput);
        eventRepository.save(eventFromDb);
        log.info("Информация о выбранном событии успешно обновлена!");
        return eventMapper.eventToEventFullDto(eventFromDb);
    }

    private void validateEventInTimeToUpdateLikeUser(Event eventFromDb, UpdateEventUserRequest updateEventInput) {
        if (eventFromDb.getState().equals(State.PUBLISHED)) {
            log.error("Нельзя изменить уже опубликованные события!");
            throw new IncorrectDataException("Внимание! Нельзя изменить уже опубликованные события!");
        }
        if (updateEventInput.getAnnotation() != null) {
            if (updateEventInput.getAnnotation().length() < 20 || updateEventInput.getAnnotation().length() > 2000) {
                log.error("Длина аннотации не может быть меньше 20 и больше 2000 символов!");
                throw new BadRequestException("Внимание! Длина аннотации не может быть меньше 20 и больше 2000 символов!");
            }
            eventFromDb.setAnnotation(updateEventInput.getAnnotation());
        }
        if (updateEventInput.getCategory() != null) {
            CategoryDto categoryDto = categoryService.getCategory(updateEventInput.getCategory());
            Category category = categoryMapper.categoryDtoToCategory(categoryDto);
            eventFromDb.setCategory(category);
        }
        if (updateEventInput.getDescription() != null) {
            if (updateEventInput.getDescription().length() < 20 || updateEventInput.getDescription().length() > 7000) {
                log.error("Длина описания не может быть меньше 20 и больше 7000 символов!");
                throw new BadRequestException("Внимание! Длина описания не может быть меньше 20 и больше 7000 символов!");
            }
            eventFromDb.setDescription(updateEventInput.getDescription());
        }
        if (updateEventInput.getEventDate() != null) {
            LocalDateTime eventDate = LocalDateTime.parse(updateEventInput.getEventDate(), DATE_TIME_FORMATTER);
            if (eventDate.isBefore(LocalDateTime.now().plusHours(2))) {
                log.error("Дата и время на которые намечено событие не может быть раньше, чем через два часа от " +
                        "текущего момента!");
                throw new BadRequestException("Внимание! Дата и время на которые намечено событие не может быть " +
                        "раньше, чем через два часа от текущего момента!");
            }
            eventFromDb.setEventDate(eventDate);
        }
        if (updateEventInput.getLocation() != null) {
            Location location = locationMapper.locationDtoToLocation(updateEventInput.getLocation());
            locationRepository.save(location);
            eventFromDb.setLocation(location);
        }
        if (updateEventInput.getPaid() != null) {
            eventFromDb.setPaid(updateEventInput.getPaid());
        }
        if (updateEventInput.getParticipantLimit() != null) {
            if (updateEventInput.getParticipantLimit() < 0) {
                log.error("Лимит участников не может быть отрицательным!");
                throw new BadRequestException("Внимание! Лимит участников не может быть отрицательным!");
            }
            eventFromDb.setParticipantLimit(updateEventInput.getParticipantLimit());
        }
        if (updateEventInput.getRequestModeration() != null) {
            eventFromDb.setRequestModeration(updateEventInput.getRequestModeration());
        }
        if (updateEventInput.getStateAction() != null) {
            switch (updateEventInput.getStateAction()) {
                case "SEND_TO_REVIEW":
                    eventFromDb.setState(State.PENDING);
                    break;
                case "CANCEL_REVIEW":
                    eventFromDb.setState(State.CANCELED);
                    break;
                default:
                    throw new BadRequestException("Внимание! Передан неверный статус события!");
            }
        }
        if (updateEventInput.getTitle() != null) {
            if (updateEventInput.getTitle().length() < 3 || updateEventInput.getTitle().length() > 120) {
                log.error("Длина заголовка не может быть меньше 3 и больше 120 символов!");
                throw new BadRequestException("Внимание! Длина заголовка не может быть меньше 3 и больше 120 символов!");
            }
            eventFromDb.setTitle(updateEventInput.getTitle());
        }
    }

    @Override
    public EventFullDto updateEventLikeUser(UpdateEventUserRequest eventDtoInput, long userId, long eventId) {
        userService.getUser(userId);
        Event eventFromDb = getEventFromDb(eventId);
        if (eventFromDb.getInitiator().getId() != userId) {
            log.error("Только пользователь, разместивший событие, может его изменять!");
            throw new BadRequestException("Внимание! Только пользователь, разместивший событие, может его изменять!");
        }
        validateEventInTimeToUpdateLikeUser(eventFromDb, eventDtoInput);
        eventRepository.save(eventFromDb);
        log.info("Информация о выбранном событии успешно обновлена!");
        return eventMapper.eventToEventFullDto(eventFromDb);
    }

    private Event getEventFromDb(long id) {
        return eventRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException("Внимание! События с таким уникальным номером не существует!"));
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventShortDto> getEventsOfCurrentUser(long userId, Integer from, Integer size) {
        if (!userRepository.existsById(userId)) {
            log.error("Пользователя с таким уникальным номером не существует!");
            throw new EntityNotFoundException("Внимание! Пользователя с таким уникальным номером не существует!");
        }
        Pageable pageable = PageRequest.of(from, size);
        return eventRepository.findAllByInitiatorId(userId, pageable).getContent().stream()
                .map(eventMapper::eventToEventShortDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public EventFullDto getEventOfCurrentUserByEventId(long userId, long eventId) {
        if (!userRepository.existsById(userId)) {
            log.error("Пользователя с таким уникальным номером не существует!");
            throw new EntityNotFoundException("Внимание! Пользователя с таким уникальным номером не существует!");
        }
        getEventFromDb(eventId);
        Optional<Event> event = eventRepository.findByIdAndInitiatorId(eventId, userId);
        if (event.isEmpty()) {
            throw new EntityNotFoundException("Запрошенное событие не найдено.");
        } else {
            return eventMapper.eventToEventFullDto(event.get());
        }
    }

    @Override
    public EventFullDto getEventById(long id) {
        Event eventFromDb = getEventFromDb(id);
        String nowAsString = LocalDateTime.now().format(DATE_TIME_FORMATTER);
        statisticsClient.createHit(new StatisticsModelDtoInput("main-service", httpServletRequest.getRequestURI(),
                httpServletRequest.getRemoteAddr(), nowAsString));
        List<String> uris = new ArrayList<>();
        uris.add(httpServletRequest.getRequestURI());
        List<String> uri = new ArrayList<>();
        for (String s : uris) {
            uri.add(s.replace("[", "").replace("]", ""));
        }
        ResponseEntity<Object> response = statisticsClient.getStatistics(eventFromDb.getCreatedOn().format(DATE_TIME_FORMATTER),
                nowAsString, uri, true);
        if (response.getStatusCode() == HttpStatus.OK) {
            List<StatisticsModelDtoOutput> statistics = objectMapper.convertValue(response.getBody(),
                    new TypeReference<List<StatisticsModelDtoOutput>>() {
                    });
            int viewsSum = 0;
            for (StatisticsModelDtoOutput statisticsModelDtoOutput : statistics) {
                viewsSum = viewsSum + statisticsModelDtoOutput.getHits();
                eventFromDb.setViews(viewsSum);
            }
        }
        log.info("Успешно получена информация о выбранном событии!");
        eventRepository.save(eventFromDb);
        return eventMapper.eventToEventFullDto(eventFromDb);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventShortDto> getShortEventInfoByParameters(String text, List<Long> categories, Boolean paid,
                                                             String rangeStart, String rangeEnd, Boolean onlyAvailable,
                                                             String sort, Integer from, Integer size) {
        String nowAsString = LocalDateTime.now().toString();
        statisticsClient.createHit(new StatisticsModelDtoInput("main-service,", httpServletRequest.getRequestURI(),
                httpServletRequest.getRemoteAddr(), nowAsString));
        List<String> uris = new ArrayList<>();
        uris.add(httpServletRequest.getRequestURI());
        List<Event> allPublishedEvents = eventRepository.findAllByStateEquals(State.PUBLISHED).stream()
                .peek(event -> {
                    int confirmedYet = requestRepository.findAllByEventIdAndStatusEquals(event.getId(), Status.CONFIRMED)
                            .size();
                    event.setConfirmedRequests(confirmedYet);
                }).collect(Collectors.toList());
        if (text != null) {
            List<Event> eventsByText =
                    eventRepository.findAllByAnnotationContainingIgnoreCaseOrDescriptionContainingIgnoreCase(text, text);
            if (!eventsByText.isEmpty()) {
                allPublishedEvents = allPublishedEvents.stream()
                        .filter(eventsByText::contains)
                        .collect(Collectors.toList());
            }
        }
        if (categories != null) {
            List<Event> eventsByIds = eventRepository.findAllByCategoryIdIn(categories);
            if (!eventsByIds.isEmpty()) {
                allPublishedEvents = allPublishedEvents.stream()
                        .filter(eventsByIds::contains)
                        .collect(Collectors.toList());
            }
        }
        if (paid != null) {
            List<Event> eventsByPaid = eventRepository.findAllByPaid(paid);
            if (!eventsByPaid.isEmpty()) {
                allPublishedEvents = allPublishedEvents.stream()
                        .filter(eventsByPaid::contains)
                        .collect(Collectors.toList());
            }
        }
        if (rangeStart == null && rangeEnd == null) {
            List<Event> afterThenNowEvents = eventRepository.findAllByEventDateIsAfter(LocalDateTime.now());
            if (!afterThenNowEvents.isEmpty()) {
                allPublishedEvents = allPublishedEvents.stream()
                        .filter(afterThenNowEvents::contains)
                        .collect(Collectors.toList());
            }
        }
        if (rangeStart != null && rangeEnd != null) {
            if (LocalDateTime.parse(rangeStart, DATE_TIME_FORMATTER)
                    .isAfter(LocalDateTime.parse(rangeEnd, DATE_TIME_FORMATTER))) {
                log.error("Передано неверное значение диапозона дат!");
                throw new BadRequestException("Внимание! Передано неверное значение диапозона дат!");
            }
        }
        if (rangeStart != null) {
            LocalDateTime start = LocalDateTime.parse(rangeStart, DATE_TIME_FORMATTER);
            List<Event> eventsByStart = eventRepository.findAllByEventDateIsAfterOrEventDateEquals(start, start);
            if (!eventsByStart.isEmpty()) {
                allPublishedEvents = allPublishedEvents.stream()
                        .filter(eventsByStart::contains)
                        .collect(Collectors.toList());
            }
        }
        if (rangeEnd != null) {
            LocalDateTime end = LocalDateTime.parse(rangeEnd, DATE_TIME_FORMATTER);
            List<Event> eventsByEnd = eventRepository.findAllByEventDateIsBeforeOrEventDateEquals(end, end);
            if (!eventsByEnd.isEmpty()) {
                allPublishedEvents = allPublishedEvents.stream()
                        .filter(eventsByEnd::contains)
                        .collect(Collectors.toList());
            }
        }
        if (onlyAvailable != null) {
            if (!allPublishedEvents.isEmpty()) {
                allPublishedEvents = allPublishedEvents.stream()
                        .filter(event -> event.getConfirmedRequests() <= event.getParticipantLimit())
                        .collect(Collectors.toList());
            }
        }
        if (sort != null) {
            switch (sort) {
                case "EVENT_DATE":
                    return allPublishedEvents.stream()
                            .sorted(Comparator.comparing(Event::getEventDate))
                            .map(eventMapper::eventToEventShortDto)
                            .limit(size)
                            .collect(Collectors.toList());
                case "VIEWS":
                    return allPublishedEvents.stream()
                            .sorted(Comparator.comparingInt(Event::getViews))
                            .map(eventMapper::eventToEventShortDto)
                            .limit(size)
                            .collect(Collectors.toList());
            }
        }
        for (Event event : allPublishedEvents) {
            ResponseEntity<Object> response = statisticsClient.getStatistics(event.getCreatedOn().toString(),
                    nowAsString, uris, true);
            if (response.getStatusCode() == HttpStatus.OK) {
                List<StatisticsModelDtoOutput> statistics = objectMapper.convertValue(response.getBody(),
                        new TypeReference<List<StatisticsModelDtoOutput>>() {
                        });
                int viewsSum = 0;
                for (StatisticsModelDtoOutput statisticsModelDtoOutput : statistics) {
                    viewsSum = viewsSum + statisticsModelDtoOutput.getHits();
                    event.setViews(viewsSum);
                }
            }
        }
        eventRepository.saveAll(allPublishedEvents);
        return allPublishedEvents.stream()
                .map(eventMapper::eventToEventShortDto)
                .limit(size)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventFullDto> getAllEventInfoByParameters(List<Long> users, List<String> states, List<Long> categories,
                                                          String rangeStart, String rangeEnd, Integer from, Integer size) {
        Pageable pageable = PageRequest.of(from, size);
        List<Event> allEvents = eventRepository.findAll(pageable).getContent();
        if (users != null) {
            List<Event> eventsByUsers = eventRepository.findAllByInitiatorIdIn(users);
            if (!eventsByUsers.isEmpty()) {
                allEvents = allEvents.stream()
                        .filter(eventsByUsers::contains)
                        .collect(Collectors.toList());
            }
        }
        if (states != null) {
            List<State> stateList = states.stream()
                    .map(State::valueOf)
                    .collect(Collectors.toList());
            List<Event> eventsByStates = eventRepository.findAllByStateIn(stateList);
            if (!eventsByStates.isEmpty()) {
                allEvents = allEvents.stream()
                        .filter(eventsByStates::contains)
                        .collect(Collectors.toList());
            }
        }
        if (categories != null) {
            List<Event> eventsByIds = eventRepository.findAllByCategoryIdIn(categories);
            if (!eventsByIds.isEmpty()) {
                allEvents = allEvents.stream()
                        .filter(eventsByIds::contains)
                        .collect(Collectors.toList());
            }
        }
        if (rangeStart == null && rangeEnd == null) {
            List<Event> afterThenNowEvents = eventRepository.findAllByEventDateIsAfter(LocalDateTime.now());
            if (!afterThenNowEvents.isEmpty()) {
                allEvents = allEvents.stream()
                        .filter(afterThenNowEvents::contains)
                        .collect(Collectors.toList());
            }
        }
        if (rangeStart != null) {
            LocalDateTime start = LocalDateTime.parse(rangeStart, DATE_TIME_FORMATTER);
            List<Event> eventsByStart = eventRepository.findAllByEventDateIsAfterOrEventDateEquals(start, start);
            if (!eventsByStart.isEmpty()) {
                allEvents = allEvents.stream()
                        .filter(eventsByStart::contains)
                        .collect(Collectors.toList());
            }
        }
        if (rangeEnd != null) {
            LocalDateTime end = LocalDateTime.parse(rangeEnd, DATE_TIME_FORMATTER);
            List<Event> eventsByEnd = eventRepository.findAllByEventDateIsBeforeOrEventDateEquals(end, end);
            if (!eventsByEnd.isEmpty()) {
                allEvents = allEvents.stream()
                        .filter(eventsByEnd::contains)
                        .collect(Collectors.toList());
            }
        }
        return allEvents.stream()
                .peek(event -> {
                    int confirmedYet = requestRepository.findAllByEventIdAndStatusEquals(event.getId(), Status.CONFIRMED)
                            .size();
                    event.setConfirmedRequests(confirmedYet);
                })
                .map(eventMapper::eventToEventFullDto)
                .collect(Collectors.toList());
    }
}
