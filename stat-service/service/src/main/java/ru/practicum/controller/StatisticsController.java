package ru.practicum.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.StatisticsModelDtoInput;
import ru.practicum.StatisticsModelDtoOutput;
import ru.practicum.service.StatisticsService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Validated
public class StatisticsController {

    private final StatisticsService statisticsService;

    @PostMapping("/hit")
    @ResponseStatus(HttpStatus.CREATED)
    public void addStatistic(@Valid @RequestBody StatisticsModelDtoInput statisticsModelDtoInput) {
        statisticsService.addStatistic(statisticsModelDtoInput);
    }

    @GetMapping("/stats")
    public List<StatisticsModelDtoOutput> getStatistics(@RequestParam String start,
                                                        @RequestParam String end,
                                                        @RequestParam(required = false) List<String> uris,
                                                        @RequestParam(defaultValue = "false") Boolean unique) {
        return statisticsService.getStatistics(start, end, uris, unique);
    }
}
