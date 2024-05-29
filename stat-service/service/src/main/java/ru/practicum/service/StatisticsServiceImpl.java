package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.StatisticsDtoOutput;
import ru.practicum.StatisticsModelDtoInput;
import ru.practicum.StatisticsModelDtoOutput;
import ru.practicum.exception.IncorrectDateTimeException;
import ru.practicum.mapper.StatisticsMapper;
import ru.practicum.model.Statistic;
import ru.practicum.repository.StatisticsRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class StatisticsServiceImpl implements StatisticsService {

    private final StatisticsMapper statisticsMapper;
    private final StatisticsRepository statisticsRepository;
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public void addStatistic(StatisticsModelDtoInput statisticsModelDtoInput) {
        Statistic statistic = statisticsMapper.statisticsModelDtoInputToStatistic(statisticsModelDtoInput);
        statisticsRepository.save(statistic);
    }

    private List<StatisticsModelDtoOutput> getStatisticByUris(List<StatisticsModelDtoOutput> statistics, List<String> uris) {
        List<StatisticsModelDtoOutput> result = new ArrayList<>();
        for (StatisticsModelDtoOutput statistic : statistics) {
            for (String uri : uris) {
                if (statistic.getUri().equals(uri)) {
                    result.add(statistic);
                }
            }
        }
        result.sort((statisticsModelDtoOutput1, statisticsModelDtoOutput2) -> {
            if (statisticsModelDtoOutput1.getHits() < statisticsModelDtoOutput2.getHits()) {
                return 1;
            } else if (statisticsModelDtoOutput1.getHits() > statisticsModelDtoOutput2.getHits()) {
                return -1;
            }
            return 0;
        });
        return result;
    }

    private void validateParameters(String start, String end) {
        LocalDateTime startTime = LocalDateTime.parse(start, DATE_TIME_FORMATTER);
        LocalDateTime endTime = LocalDateTime.parse(end, DATE_TIME_FORMATTER);
        if (endTime.isBefore(startTime)) {
            throw new IncorrectDateTimeException("Внимание! Дата окончания периода не может быть раньше даты его начала!");
        }
    }

    private List<StatisticsModelDtoOutput> setHits(List<StatisticsDtoOutput> statistics) {
        List<StatisticsModelDtoOutput> statisticsWithHits = new ArrayList<>();
        for (StatisticsDtoOutput statistic : statistics) {
            long hits = statistics.stream()
                    .filter(statisticsDtoOutput -> statisticsDtoOutput.getUri().equals(statistic.getUri())
                            && statisticsDtoOutput.getIp().equals(statistic.getIp())).count();
            StatisticsModelDtoOutput statisticsModelDtoOutput = statisticsMapper.statisticsDtoOutputToFinalDto(statistic);
            statisticsModelDtoOutput.setHits((int) hits);
            statisticsWithHits.add(statisticsModelDtoOutput);
        }
        statisticsWithHits.sort((statisticsModelDtoOutput1, statisticsModelDtoOutput2) -> {
            if (statisticsModelDtoOutput1.getHits() < statisticsModelDtoOutput2.getHits()) {
                return 1;
            } else if (statisticsModelDtoOutput1.getHits() > statisticsModelDtoOutput2.getHits()) {
                return -1;
            }
            return 0;
        });
        return statisticsWithHits.stream().distinct().collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<StatisticsModelDtoOutput> getStatistics(String start, String end, List<String> uris, Boolean unique) {
        validateParameters(start, end);
        if (uris == null || uris.isEmpty()) {
            if (unique) {
                return setHits(statisticsRepository.findStatisticByStartAndEndIfIpIsUnique(start, end));
            }
            return setHits(statisticsRepository.findStatisticByStartAndEnd(start, end));
        }
        if (unique) {
            return getStatisticByUris(setHits(statisticsRepository.findStatisticByStartAndEndIfIpIsUnique(start, end)),
                    uris);
        }
        return getStatisticByUris(setHits(statisticsRepository.findStatisticByStartAndEnd(start, end)), uris);
    }
}
