package ru.practicum.service.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.exception.EntityNotFoundException;
import ru.practicum.exception.IncorrectDataException;
import ru.practicum.mapper.UserMapper;
import ru.practicum.dto.user.NewUserRequest;
import ru.practicum.dto.user.UserDto;
import ru.practicum.model.User;
import ru.practicum.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public UserDto addUser(NewUserRequest userDtoInput) {
        if (!userRepository.findAllByEmailIgnoreCase(userDtoInput.getEmail()).isEmpty()) {
            log.error("Пользователь с таким именем уже существует!");
            throw new IncorrectDataException("Внимание! Пользователь с таким именем уже существует!");
        }
        User newUser = userMapper.newUserRequestToUser(userDtoInput);
        userRepository.save(newUser);
        log.info("Информация о новом пользователе успешно сохранена!");
        return userMapper.userToUserDto(newUser);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDto> getUsers(List<Long> ids, Integer from, Integer size) {
        Pageable pageable = PageRequest.of(from, size);
        if (ids == null) {
            return userRepository.findAll(pageable).stream()
                    .map(userMapper::userToUserDto)
                    .collect(Collectors.toList());
        }
        return userRepository.findAllByIdIn(ids, pageable).stream()
                .map(userMapper::userToUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto getUser(long userId) {
        return userMapper.userToUserDto(getUserFromDb(userId));
    }

    private User getUserFromDb(long id) {
        return userRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException("Внимание! Пользователя с таким уникальным номером не существует!"));
    }

    @Override
    public void deleteUser(long userId) {
        if (!userRepository.existsById(userId)) {
            log.error("Пользователя с таким уникальным номером не существует!");
            throw new EntityNotFoundException("Внимание! Пользователя с таким уникальным номером не существует!");
        } else {
            userRepository.deleteById(userId);
            log.info("Информация о пользователе успешно удалена!");
        }
    }
}
