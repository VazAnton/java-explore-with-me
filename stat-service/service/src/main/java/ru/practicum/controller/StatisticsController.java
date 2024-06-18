package ru.practicum.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.StatisticsModelDtoInput;
import ru.practicum.StatisticsModelDtoOutput;
import ru.practicum.service.StatisticsService;

import javax.validation.Valid;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
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
        String decodedStart = URLDecoder.decode(start, StandardCharsets.UTF_8);
        String decodedEnd = URLDecoder.decode(end, StandardCharsets.UTF_8);
        return statisticsService.getStatistics(decodedStart, decodedEnd, uris, unique);
    }
}
