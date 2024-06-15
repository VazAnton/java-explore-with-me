package ru.practicum.mapper;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.model.event.Event;
import ru.practicum.model.dto.event.EventFullDto;
import ru.practicum.model.dto.event.EventShortDto;
import ru.practicum.model.dto.event.NewEventDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Mapper(componentModel = "spring", uses = {LocationMapper.class, UserMapper.class, CategoryMapper.class},
        injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public abstract class AbstractEventMapper {

    private static final DateTimeFormatter EVENT_DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Mapping(source = "eventDate", target = "eventDate", dateFormat = "yyyy-MM-dd HH:mm:ss")
    public abstract EventFullDto eventToEventFullDto(Event event);

    @Mapping(source = "eventDate", target = "eventDate", dateFormat = "yyyy-MM-dd HH:mm:ss")
    public abstract EventShortDto eventToEventShortDto(Event event);

    public Event newEventDtoToEvent(NewEventDto eventDtoInput) {
        if (eventDtoInput == null) {
            return null;
        }
        Event event = new Event();
        event.setAnnotation(eventDtoInput.getAnnotation());
        event.setDescription(eventDtoInput.getDescription());
        event.setEventDate(LocalDateTime.parse(eventDtoInput.getEventDate(), EVENT_DATE_FORMATTER));
        event.setPaid(eventDtoInput.getPaid());
        event.setParticipantLimit(eventDtoInput.getParticipantLimit());
        event.setRequestModeration(eventDtoInput.getRequestModeration());
        event.setTitle(eventDtoInput.getTitle());
        return event;
    }

    public Event eventFullDtoToEvent(EventFullDto eventFullDto) {
        if (eventFullDto == null) {
            return null;
        }
        Event event = new Event();
        event.setId(eventFullDto.getId());
        event.setAnnotation(eventFullDto.getAnnotation());
        //CategoryMapper categoryMapper = new CategoryMapperImpl();
        //event.setCategory(categoryMapper.categoryDtoToCategory(eventFullDto.getCategory()));
        event.setConfirmedRequests(eventFullDto.getConfirmedRequests());
        event.setCreatedOn(LocalDateTime.parse(eventFullDto.getCreatedOn(), DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        event.setDescription(eventFullDto.getDescription());
        event.setEventDate(LocalDateTime.parse(eventFullDto.getEventDate(), EVENT_DATE_FORMATTER));
        //UserMapper userMapper = new UserMapperImpl();
        //event.setInitiator(userMapper.userShortDtoToUser(eventFullDto.getInitiator()));
        //LocationMapper locationMapper = new LocationMapperImpl();
        //event.setLocation(locationMapper.locationDtoToLocation(eventFullDto.getLocation()));
        event.setPaid(eventFullDto.getPaid());
        event.setParticipantLimit(eventFullDto.getParticipantLimit());
        if (eventFullDto.getPublishedOn() != null) {
            event.setPublishedOn(LocalDateTime.parse(eventFullDto.getPublishedOn(), DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        }
        event.setRequestModeration(eventFullDto.getRequestModeration());
        event.setState(eventFullDto.getState());
        event.setTitle(eventFullDto.getTitle());
        event.setViews(eventFullDto.getViews());
        return event;
    }
}
