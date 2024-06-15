package ru.practicum.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.model.dto.user.UserDto;
import ru.practicum.model.user.User;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {

    List<User> findAllByEmailIgnoreCase(String email);

//    @Query(value = "SELECT new ru.practicum.model.dto.user.UserDto(u.id, u.name, u.email)" +
//                    "FROM User AS u " +
//                    "WHERE u.id IN ?1")
//    Page<UserDto> getAllUsersBiIds(List<Long> ids, Pageable pageable);

    Page<User> findAllByIdIn(List<Long> ids, Pageable pageable);
}
