package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.StatisticsDtoOutput;
import ru.practicum.model.Statistic;

import java.util.List;

public interface StatisticsRepository extends JpaRepository<Statistic, Long> {

    @Query(nativeQuery = true,
            value = "SELECT s.app, " +
                    "s.uri, " +
                    "s.ip " +
                    "FROM statistics AS s " +
                    "WHERE CAST(s.created AS timestamp) BETWEEN CAST(?1 AS timestamp) AND CAST(?2 AS timestamp);")
    List<StatisticsDtoOutput> findStatisticByStartAndEnd(String start, String end);

    @Query(nativeQuery = true,
            value = "SELECT s.app, " +
                    "s.uri, " +
                    "s.ip, " +
                    "COUNT(DISTINCT s.ip) " +
                    "FROM statistics AS s  " +
                    "WHERE CAST(s.created AS timestamp) BETWEEN CAST(?1 AS timestamp) AND CAST(?2 AS timestamp) " +
                    "GROUP BY s.app, s.ip, s.uri " +
                    "ORDER BY COUNT(DISTINCT s.ip) DESC;")
    List<StatisticsDtoOutput> findStatisticByStartAndEndIfIpIsUnique(String start, String end);

    @Query(nativeQuery = true,
            value = "SELECT s.statistic_id, " +
                    "s.app, " +
                    "s.uri, " +
                    "s.ip, " +
                    "s.created, " +
                    "COUNT(s.ip) AS hits " +
                    "FROM statistics AS s " +
                    "WHERE s.uri LIKE ?3 " +
                    "AND CAST(s.created AS timestamp) BETWEEN CAST(?1 AS timestamp) AND CAST(?2 AS timestamp) " +
                    "GROUP BY s.statistic_id, s.app, s.ip " +
                    "ORDER BY CAST(s.created AS timestamp) DESC;")
    List<StatisticsDtoOutput> findStatisticByStartAndEndAndUrisInIfIpIsUnique(String start, String end, List<String> uris);

    @Query(nativeQuery = true,
            value = "SELECT s.statistic_id, " +
                    "s.app, " +
                    "s.uri, " +
                    "s.ip, " +
                    "s.created, " +
                    "COUNT(s.ip) AS hits " +
                    "FROM statistics AS s " +
                    "WHERE s.uri LIKE ?3 " +
                    "AND CAST(s.created AS timestamp) BETWEEN CAST(?1 AS timestamp) AND CAST(?2 AS timestamp) " +
                    "GROUP BY s.statistic_id " +
                    "ORDER BY CAST(s.created AS timestamp) DESC;")
    List<StatisticsDtoOutput> findStatisticByStartAndEndAndUrisIn(String start, String end, List<String> uris);

    @Query(value = "SELECT s.id, " +
            "s.app, " +
            "s.uri, " +
            "s.ip, " +
            "s.timestamp " +
            "FROM Statistic AS s " +
            "WHERE s.uri IN :uris")
    List<Statistic> findAllStatisticsByUris(List<String> uris);

    List<Statistic> findAllByUriIn(List<String> uris);

    List<Statistic> findDistinctByIpAndUri(String ip, String uri);
}
