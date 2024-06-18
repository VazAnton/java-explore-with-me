package ru.practicum.mapper;

import org.mapstruct.Mapper;
import ru.practicum.model.dto.location.LocationDto;
import ru.practicum.model.location.Location;

@Mapper(componentModel = "spring")
public interface LocationMapper {

    Location locationDtoToLocation(LocationDto locationDto);

    LocationDto locationToLocationDto(Location location);
}
