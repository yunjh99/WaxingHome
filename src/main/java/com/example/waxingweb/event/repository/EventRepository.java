package com.example.waxingweb.event.repository;

import com.example.waxingweb.event.domain.Event;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    // 현재 진행 중인 이벤트만 조회
    @Query("""
    SELECT e
    FROM Event e
    WHERE e.startDate <= :currentDate   
      AND e.endDate   >= :currentDate
      AND e.deletedAt IS NULL
    ORDER BY e.createdAt DESC
    """)
    Page<Event> findAllActiveEvents(LocalDate currentDate, Pageable pageable);

    @Query("SELECT e FROM Event e WHERE e.endDate < :currentDate AND e.deletedAt IS NULL")
    Page<Event> findAllExpiredEvents(LocalDate currentDate, Pageable pageable);
}
