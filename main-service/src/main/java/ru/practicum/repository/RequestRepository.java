package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.model.enums.Status;
import ru.practicum.model.request.Request;

import java.util.List;
import java.util.Optional;

public interface RequestRepository extends JpaRepository<Request, Long> {

    List<Request> findAllByUserId(long userId);

    List<Request> findAllByEventId(long eventId);

    List<Request> findAllByEventIdAndStatusEquals(long eventId, Status status);

    Optional<Request> findByEventIdAndUserId(long eventId, long userId);

    List<Request> findAllByIdIn(List<Long> requestsIds);
}
