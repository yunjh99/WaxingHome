package com.example.waxingweb.event.domain;

import com.example.waxingweb.file.domain.UploadFile;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EventImage {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EventImageType type;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "upload_file_id", nullable = false)
    private UploadFile uploadFile;

    /**
     * EventImage 생성 팩토리 메서드
     *
     * - Event ↔ EventImage 양방향 연관관계를 한 번에 완결
     * - 썸네일 / 본문 이미지 타입 구분 포함
     */
    public static EventImage create(Event event,
                                    EventImageType type,
                                    UploadFile uploadFile) {

        EventImage ei = new EventImage();
        ei.event = event;
        ei.type = type;
        ei.uploadFile = uploadFile;

        // ✅ 양방향 연관관계 완결
        event.addImage(ei);

        return ei;
    }

    public void changeFile(UploadFile newFile) {
        this.uploadFile = newFile;
    }
}
