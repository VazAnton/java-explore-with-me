package ru.practicum.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.StatisticsDtoOutput;
import ru.practicum.StatisticsModelDtoInput;
import ru.practicum.StatisticsModelDtoOutput;
import ru.practicum.model.Statistic;

@Component
public class StatisticsMapper {

    public Statistic statisticsModelDtoInputToStatistic(StatisticsModelDtoInput statisticsModelDtoInput) {
        if (statisticsModelDtoInput == null) {
            return null;
        }
        Statistic statistic = new Statistic();
        statistic.setApp(statisticsModelDtoInput.getApp());
        statistic.setUri(statisticsModelDtoInput.getUri());
        statistic.setIp(statisticsModelDtoInput.getIp());
        statistic.setTimestamp(statisticsModelDtoInput.getTimestamp());
        return statistic;
    }

    public StatisticsModelDtoOutput statisticToStatisticsModelDtoOutput(Statistic statistic) {
        if (statistic == null) {
            return null;
        }
        StatisticsModelDtoOutput statisticsModelDto = new StatisticsModelDtoOutput();
        statisticsModelDto.setApp(statistic.getApp());
        statisticsModelDto.setUri(statistic.getUri());
        return statisticsModelDto;
    }

    public StatisticsModelDtoOutput statisticsDtoOutputToFinalDto(StatisticsDtoOutput statisticsDtoOutput) {
        if (statisticsDtoOutput == null) {
            return null;
        }
        StatisticsModelDtoOutput statisticsModelDto = new StatisticsModelDtoOutput();
        statisticsModelDto.setApp(statisticsDtoOutput.getApp());
        statisticsModelDto.setUri(statisticsDtoOutput.getUri());
        return statisticsModelDto;
    }
}
