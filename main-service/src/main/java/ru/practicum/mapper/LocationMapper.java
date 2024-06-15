package ru.practicum.mapper;

import org.mapstruct.Mapper;
import ru.practicum.model.location.Location;
import ru.practicum.model.dto.location.LocationDto;

@Mapper(componentModel = "spring")
public interface LocationMapper {

    Location locationDtoToLocation(LocationDto locationDto);

    LocationDto locationToLocationDto(Location location);
}
