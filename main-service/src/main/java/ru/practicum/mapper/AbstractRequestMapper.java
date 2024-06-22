package ru.practicum.mapper;

import org.mapstruct.Mapper;
import ru.practicum.dto.request.ParticipationRequestDto;
import ru.practicum.model.Request;

import java.time.format.DateTimeFormatter;

@Mapper(componentModel = "spring")
public abstract class AbstractRequestMapper {

    private static final DateTimeFormatter CREATED_ON_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-ddТHH:mm:ss.SSS");

    public ParticipationRequestDto requestToParticipationRequestDto(Request request) {
        if (request == null) {
            return null;
        }
        ParticipationRequestDto participationRequestDto = new ParticipationRequestDto();
        participationRequestDto.setId(request.getId());
        participationRequestDto.setCreated(request.getCreated().format(CREATED_ON_FORMATTER));
        participationRequestDto.setEvent(request.getEvent().getId());
        participationRequestDto.setRequester(request.getUser().getId());
        participationRequestDto.setStatus(String.valueOf(request.getStatus()));
        return participationRequestDto;
    }
}
