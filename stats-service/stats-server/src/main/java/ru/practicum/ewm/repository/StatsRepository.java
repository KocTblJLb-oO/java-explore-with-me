package ru.practicum.ewm.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.ewm.model.EndpointHit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import ru.practicum.ewm.dto.ViewStats;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface StatsRepository extends JpaRepository<EndpointHit, Long> {

    @Query("select new ru.practicum.ewm.dto.ViewStats(eh.app, eh.uri, count(eh)) " +
            "from EndpointHit eh " +
            "where eh.timestamp between :start and :end " +
            "and (:uris is null or eh.uri in :uris) " +
            "group by eh.app, eh.uri " +
            "order by 3 desc ")
    List<ViewStats> getStats(@Param("start") LocalDateTime start,
                             @Param("end") LocalDateTime end,
                             @Param("uris") List<String> uris);

    @Query("select new ru.practicum.ewm.dto.ViewStats(eh.app, eh.uri, count(distinct eh.ip)) " +
            "from EndpointHit eh " +
            "where eh.timestamp between :start and :end " +
            "and (:uris is null or eh.uri in :uris) " +
            "group by eh.app, eh.uri " +
            "order by 3 desc ")
    List<ViewStats> getUniqueStats(@Param("start") LocalDateTime start,
                                   @Param("end") LocalDateTime end,
                                   @Param("uris") List<String> uris);
}
