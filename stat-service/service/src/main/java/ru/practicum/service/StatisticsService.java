package ru.practicum.service;

import ru.practicum.StatisticsModelDtoInput;
import ru.practicum.StatisticsModelDtoOutput;

import java.util.List;

public interface StatisticsService {

    void addStatistic(StatisticsModelDtoInput statisticsModelDtoInput);

    List<StatisticsModelDtoOutput> getStatistics(String start, String end, List<String> uris, Boolean unique);
}
