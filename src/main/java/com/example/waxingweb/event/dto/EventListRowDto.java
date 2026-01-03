package com.example.waxingweb.event.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class EventListRowDto {
    private Long id;
    private String title;
    private LocalDate startDate;
    private LocalDate endDate;
    private Long thumbnailFileId;   // ✅ 추가
}
