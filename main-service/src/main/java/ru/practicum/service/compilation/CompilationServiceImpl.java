package ru.practicum.service.compilation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.exception.EntityNotFoundException;
import ru.practicum.mapper.AbstractEventMapper;
import ru.practicum.mapper.CompilationMapper;
import ru.practicum.model.compilation.Compilation;
import ru.practicum.model.dto.compilation.CompilationDto;
import ru.practicum.model.dto.compilation.NewCompilationDto;
import ru.practicum.model.dto.compilation.UpdateCompilationRequest;
import ru.practicum.model.dto.event.EventShortDto;
import ru.practicum.model.event.Event;
import ru.practicum.repository.CompilationRepository;
import ru.practicum.repository.EventRepository;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class CompilationServiceImpl implements CompilationService {

    private final CompilationRepository compilationRepository;
    private final CompilationMapper compilationMapper;
    private final EventRepository eventRepository;
    private final AbstractEventMapper eventMapper;

    private CompilationDto setEventsToCompilationDto(Compilation compilation, Set<Event> events) {
        CompilationDto compilationDto = compilationMapper.compilationToCompilationDto(compilation);
        if (events.isEmpty()) {
            compilationDto.setEvents(Collections.emptySet());
        } else {
            Set<EventShortDto> shortEvents = events.stream()
                    .map(eventMapper::eventToEventShortDto)
                    .collect(Collectors.toSet());
            compilationDto.setEvents(shortEvents);
        }
        return compilationDto;
    }

    @Override
    public CompilationDto addCompilation(NewCompilationDto compilationDtoInput) {
        Compilation compilation = compilationMapper.newCompilationDtoToCompilation(compilationDtoInput);
        Set<Event> events;
        if (compilationDtoInput.getPinned() == null) {
            compilation.setPinned(false);
        }
        if (compilationDtoInput.getEvents() != null) {
            events = eventRepository.findAllByIdIn(compilationDtoInput.getEvents());
        } else {
            events = Collections.emptySet();
        }
        compilation.setEvents(events);
        compilationRepository.save(compilation);
        log.info("Информация о новой категории успешно сохранена!");
        return setEventsToCompilationDto(compilation, events);
    }

    @Override
    public CompilationDto updateCompilation(UpdateCompilationRequest updateCompilationRequest, long compId) {
        Compilation compilationFromDb = getCompilationFromDb(compId);
        Set<Event> events;
        if (updateCompilationRequest.getPinned() != null) {
            compilationFromDb.setPinned(updateCompilationRequest.getPinned());
        }
        if (updateCompilationRequest.getTitle() != null) {
            compilationFromDb.setTitle(updateCompilationRequest.getTitle());
        }
        if (updateCompilationRequest.getEvents() != null) {
            events = eventRepository.findAllByIdIn(updateCompilationRequest.getEvents());
            compilationFromDb.setEvents(events);
        } else {
            events = Collections.emptySet();
        }
        compilationRepository.save(compilationFromDb);
        log.info("Информация о выбранной категории успешно сохранена!");
        return setEventsToCompilationDto(compilationFromDb, events);
    }

    private Compilation getCompilationFromDb(long id) {
        return compilationRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Внимание! Подборки " +
                "событий с таким уникальным номером не существует!"));
    }

    @Override
    public void deleteCompilation(long compId) {
        if (!compilationRepository.existsById(compId)) {
            log.error("Подборки событий с таким уникальным номером не существует!");
            throw new EntityNotFoundException("Внимание! Подборки событий с таким уникальным номером не существует!");
        } else {
            compilationRepository.deleteById(compId);
            log.info("Выбранная подборка событий успешно удалена!");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public CompilationDto getCompilationById(long compId) {
        Compilation compilation = getCompilationFromDb(compId);
        log.info("Успешно получена информация о выбранной подборке событий!");
        return setEventsToCompilationDto(compilation, compilation.getEvents());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CompilationDto> getCompilations(Boolean pinned, Integer from, Integer size) {
        Pageable pageable = PageRequest.of(from, size);
        if (pinned != null) {
            return compilationRepository.findAllByPinned(pinned, pageable).getContent().stream()
                    .map(compilation -> setEventsToCompilationDto(compilation, compilation.getEvents()))
                    .collect(Collectors.toList());
        }
        return compilationRepository.findAll().stream()
                .limit(size)
                .map(compilation -> setEventsToCompilationDto(compilation, compilation.getEvents()))
                .collect(Collectors.toList());
    }
}
