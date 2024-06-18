package ru.practicum.mapper;

import org.mapstruct.Mapper;
import ru.practicum.model.dto.user.NewUserRequest;
import ru.practicum.model.dto.user.UserDto;
import ru.practicum.model.dto.user.UserShortDto;
import ru.practicum.model.user.User;

@Mapper(componentModel = "spring")
public interface UserMapper {

    User userShortDtoToUser(UserShortDto userShortDto);

    User newUserRequestToUser(NewUserRequest newUserRequest);

    UserDto userToUserDto(User user);

    User userDtoToUser(UserDto userDto);

    UserShortDto userToUserShortDto(User user);
}
