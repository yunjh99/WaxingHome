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
    public Long create(User user, EventCreateRequest request) {

        Event event = Event.create(
                user,
                request.getTitle(),
                request.getContent(),
                request.getStartDate(),
                request.getEndDate()
        );

        saveImages(event, request);

        return eventRepository.save(event).getId();
    }

    private void saveImages(Event event, EventCreateRequest request) {
        saveImageIfPresent(event, request.getThumbnail(), EventImageType.THUMBNAIL);
        saveImageIfPresent(event, request.getBodyImage(), EventImageType.BODY);
    }

    private void saveImageIfPresent(Event event, MultipartFile file, EventImageType type) {
        if (file == null || file.isEmpty()) return;

        UploadFile uploadFile = fileStorageService.store(file, "event");
        EventImage.create(event, type, uploadFile);
    }


}
