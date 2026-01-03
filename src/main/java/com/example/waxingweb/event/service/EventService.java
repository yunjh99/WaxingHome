package com.example.waxingweb.event.service;

import com.example.waxingweb.event.domain.Event;
import com.example.waxingweb.event.domain.EventImage;
import com.example.waxingweb.event.domain.EventImageType;
import com.example.waxingweb.event.dto.EventCreateRequest;
import com.example.waxingweb.event.dto.EventListDto;
import com.example.waxingweb.event.dto.EventListRowDto;
import com.example.waxingweb.event.repository.EventRepository;
import com.example.waxingweb.file.domain.UploadFile;
import com.example.waxingweb.file.service.FileStorageService;
import com.example.waxingweb.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EventService {

    private final EventRepository eventRepository;
    private final FileStorageService fileStorageService;

    // 진행중인 이벤트 조회 (목록용 DTO)
    public Page<EventListDto> getActiveEvents(Pageable pageable) {
        LocalDate today = LocalDate.now();

        return eventRepository.findAllActiveEvents(today, pageable)
                .map(row -> new EventListDto(
                        row.getId(),
                        row.getTitle(),
                        row.getStartDate(),
                        row.getEndDate(),
                        row.getThumbnailFileId() != null
                                ? "/files/" + row.getThumbnailFileId()
                                : "/images/no-thumbnail.png"
                ));
    }


    //만료된 이벤트 조회
    public Page<Event> getExpiredEvents(Pageable pageable) {
        LocalDate today = LocalDate.now();
        return eventRepository.findAllExpiredEvents(today, pageable);
    }

    /**
     * 이벤트 생성
     * - Event 저장
     * - thumbnail/body 이미지가 있으면 UploadFile 저장 후 EventImage로 연결
     */
    @Transactional
    public Long create(User user,
                       EventCreateRequest request,
                       MultipartFile thumbnail,
                       MultipartFile bodyImage) {

        // 1) 이벤트 엔티티 생성
        Event event = Event.create(
                user,
                request.getTitle(),
                request.getContent(),
                request.getStartDate(),
                request.getEndDate()
        );

        // 2) 썸네일 저장 + 연결
        if (thumbnail != null && !thumbnail.isEmpty()) {
            UploadFile thumbFile = fileStorageService.store(thumbnail, "event");
            EventImage.create(event, EventImageType.THUMBNAIL, thumbFile);
        }

        // 3) 본문 이미지 저장 + 연결
        if (bodyImage != null && !bodyImage.isEmpty()) {
            UploadFile bodyFile = fileStorageService.store(bodyImage, "event");
            EventImage.create(event, EventImageType.BODY, bodyFile);
        }

        // 4) 저장 (Event -> EventImage -> UploadFile cascade로 같이 저장됨)
        Event saved = eventRepository.save(event);
        return saved.getId();
    }

}
