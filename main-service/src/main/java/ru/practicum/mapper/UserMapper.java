package ru.practicum.mapper;

import org.mapstruct.Mapper;
import ru.practicum.dto.user.NewUserRequest;
import ru.practicum.dto.user.UserDto;
import ru.practicum.dto.user.UserShortDto;
import ru.practicum.model.User;

@Mapper(componentModel = "spring")
public interface UserMapper {

    User userShortDtoToUser(UserShortDto userShortDto);

    User newUserRequestToUser(NewUserRequest newUserRequest);

    UserDto userToUserDto(User user);

    User userDtoToUser(UserDto userDto);

    UserShortDto userToUserShortDto(User user);
}
