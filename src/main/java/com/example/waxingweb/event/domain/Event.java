package com.example.waxingweb.event.domain;

import com.example.waxingweb.user.domain.User;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "event_id")
    private Long id; //기본키

    @ManyToOne(fetch = FetchType.LAZY) // LAZY: 이벤트 조회 시 작성자 정보는 필요할 때만 조회
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // 작성자

    @NotBlank // 사용자 입력 검증: null, 빈 문자열, 공백만 입력 모두 허용하지 않음
    @Column(nullable = false)
    private String title;  //제목

    @Column(columnDefinition = "TEXT")
    private String content; //내용

    @Column(nullable = false)
    private LocalDate startDate;  // 이벤트 시작일 (00:00부터 노출로 간주)

    @Column(nullable = false)
    private LocalDate endDate;    // 이벤트 종료일 (23:59:59까지 노출로 간주)

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt; // 생성일시 (수정 불가)

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt; // 수정일시

    @Column(length = 500)
    private String thumbnailPath; // 썸네일

    @Column(length = 500)
    private String bodyImagePath; // 본문 이미지
}
