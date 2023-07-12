package ru.practicum.events.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.events.model.Event;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

    Event findFirstByCategoryId(Long id);

    Set<Event> findEventByIdIn(Set<Long> ids);

    List<Event> findAllByInitiatorId(Long id, Pageable pageable);

    List<Event> findByIdAndInitiatorId(Long eventId, Long userId);

    @Query(value = "SELECT * " +
            "FROM events element " +
            "WHERE (:users IS NULL OR element.initiator_id IN (cast(cast(:users AS TEXT) AS BIGINT))) " +
            "AND (:states IS NULL OR element.state IN (cast(:states AS text))) " +
            "AND (:categories IS NULL or element.category_id IN (cast(cast(:categories AS TEXT) AS BIGINT))) " +
            "AND (cast(:rangeStart AS TIMESTAMP) IS NULL OR element.event_date >= cast(:rangeStart AS TIMESTAMP))" +
            "AND (cast(:rangeEnd AS TIMESTAMP) IS NULL OR element.event_date < cast(:rangeEnd AS TIMESTAMP))",
            nativeQuery = true)
    List<Event> findEventsByAdmin(@Param("users") List<Long> userId,
                                  @Param("states") List<String> states,
                                  @Param("categories") List<Long> categories,
                                  @Param("rangeStart") LocalDateTime rangeStart,
                                  @Param("rangeEnd") LocalDateTime rangeEnd,
                                  Pageable pageable);

    @Query(value = "SELECT * " +
            "FROM events element " +
            "WHERE (element.state = 'PUBLISHED') " +
            "AND (:text IS NULL OR lower(element.annotation) LIKE lower(concat('%',cast(:text AS text),'%')) " +
            "OR lower(element.description) LIKE lower(concat('%',cast(:text AS text),'%'))) " +
            "AND (:categories IS NULL OR element.category_id IN (cast(cast(:categories AS TEXT) AS BIGINT))) " +
            "AND (:paid IS NULL OR element.paid = cast(cast(:paid AS text) AS BOOLEAN)) " +
            "AND (element.event_date >= :rangeStart) " +
            "AND (cast(:rangeEnd AS TIMESTAMP) IS NULL OR element.event_date < cast(:rangeEnd AS TIMESTAMP))",
            nativeQuery = true)
    List<Event> findPublishedEventsByUser(@Param("text") String text,
                                          @Param("categories") List<Long> categories,
                                          @Param("paid") Boolean paid,
                                          @Param("rangeStart") LocalDateTime rangeStart,
                                          @Param("rangeEnd") LocalDateTime rangeEnd,
                                          Pageable pageable);
}