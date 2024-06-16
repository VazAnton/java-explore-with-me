package ru.practicum.service.user;

import ru.practicum.model.dto.user.NewUserRequest;
import ru.practicum.model.dto.user.UserDto;

import java.util.List;

public interface UserService {

    UserDto addUser(NewUserRequest userDtoInput);

    List<UserDto> getUsers(List<Long> ids, Integer from, Integer size);

    UserDto getUser(long userId);

    void deleteUser(long userId);
}
