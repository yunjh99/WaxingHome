package com.example.waxingweb.file.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * 공통 파일 업로드 엔티티
 *
 * - 이벤트, 공지사항, 썸네일 등 여러 도메인에서 재사용되는 파일 메타데이터
 * - 실제 파일은 서버(또는 외부 스토리지)에 저장되고,
 *   이 엔티티는 "파일 정보(메타데이터)"만 DB에서 관리한다.
 */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // JPA 프록시 생성을 위한 기본 생성자 (외부 직접 생성 제한)
public class UploadFile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 파일 식별자 (PK)

    @Column(nullable = false)
    private String originalName;
    // 사용자가 업로드한 원본 파일명
    // 화면 표시용 (다운로드 시 파일명으로 사용)

    @Column(nullable = false)
    private String storedName;
    // 서버에 실제 저장되는 파일명
    // UUID 등을 사용하여 파일명 중복 방지

    @Column(nullable = false, length = 500)
    private String path;
    // 파일이 저장된 디렉터리 경로
    // 예) /upload/event/2025-12-26

    @Column(nullable = false)
    private Long size;
    // 파일 크기 (byte 단위)
    // 업로드 제한, 통계, 검증 용도

    @Column
    private String contentType;
    // 파일 MIME 타입
    // 예) image/png, image/jpeg, application/pdf

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    // 파일 업로드(메타데이터 생성) 시각
    // 수정 불가

    /**
     * UploadFile 생성 팩토리 메서드
     *
     * @param originalName 사용자가 업로드한 파일명
     * @param storedName   서버에 저장된 파일명(UUID)
     * @param path         저장된 디렉터리 경로
     * @param size         파일 크기(byte)
     * @param contentType  MIME 타입
     */
    public static UploadFile of(String originalName, String storedName, String path,
                                Long size, String contentType) {
        UploadFile f = new UploadFile();
        f.originalName = originalName;
        f.storedName = storedName;
        f.path = path;
        f.size = size;
        f.contentType = contentType;
        return f;
    }

    /**
     * 실제 서버에 저장된 파일의 전체 경로
     *
     * - DB에는 디렉터리(path)와 파일명(storedName)을 분리 저장
     * - 파일 접근/삭제 시 조합하여 사용
     */
    @Transient
    public String key() {
        return path + "/" + storedName; // event/2025-12-26/uuid.png
    }
}
