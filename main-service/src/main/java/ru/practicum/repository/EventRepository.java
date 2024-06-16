package ru.practicum.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.model.enums.State;
import ru.practicum.model.event.Event;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface EventRepository extends JpaRepository<Event, Long> {

    List<Event> findAllByCategoryId(long catId);

    Page<Event> findAllByInitiatorId(long userId, Pageable pageable);

    Optional<Event> findByIdAndInitiatorId(long eventId, long userId);

    List<Event> findAllByAnnotationContainingIgnoreCaseOrDescriptionContainingIgnoreCase(String maybeAnnotation,
                                                                                         String maybeDescription);

    List<Event> findAllByStateEquals(State state);

    List<Event> findAllByCategoryIdIn(List<Long> ids);

    List<Event> findAllByPaid(Boolean paid);

    List<Event> findAllByEventDateIsAfter(LocalDateTime now);

    List<Event> findAllByEventDateIsAfterOrEventDateEquals(LocalDateTime after, LocalDateTime equals);

    List<Event> findAllByEventDateIsBeforeOrEventDateEquals(LocalDateTime before, LocalDateTime equals);

    Set<Event> findAllByIdIn(List<Long> ids);

    List<Event> findAllByInitiatorIdIn(List<Long> userIds);

    List<Event> findAllByStateIn(List<State> states);
}
