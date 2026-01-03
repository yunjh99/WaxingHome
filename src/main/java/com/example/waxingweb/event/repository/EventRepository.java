package com.example.waxingweb.event.repository;

import com.example.waxingweb.event.domain.Event;
import com.example.waxingweb.event.dto.EventListRowDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    // ✅ 진행중 이벤트 목록 DTO 조회 (썸네일은 UploadFile.id만)
    @Query("""
    select new com.example.waxingweb.event.dto.EventListRowDto(
        e.id,
        e.title,
        e.startDate,
        e.endDate,
        uf.id
    )
    from Event e
    left join com.example.waxingweb.event.domain.EventImage ei
           on ei.event = e
          and ei.type = com.example.waxingweb.event.domain.EventImageType.THUMBNAIL
    left join ei.uploadFile uf
    where e.deletedAt is null
      and e.startDate <= :today
      and e.endDate >= :today
    order by e.startDate desc
""")
    Page<EventListRowDto> findAllActiveEvents(@Param("today") LocalDate today, Pageable pageable);

    @Query("SELECT e FROM Event e WHERE e.endDate < :currentDate AND e.deletedAt IS NULL")
    Page<Event> findAllExpiredEvents(LocalDate currentDate, Pageable pageable);
}
