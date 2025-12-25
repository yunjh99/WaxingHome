package com.example.waxingweb.event.dto;


import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Getter
@Setter
public class EventCreateRequest {

    @NotBlank(message = "제목은 필수입니다.")
    private String title;

    private String content;

    @NotNull(message = "시작일은 필수입니다.")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;

    @NotNull(message = "종료일은 필수입니다.")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;

    @AssertTrue(message = "종료일은 시작일보다 빠를 수 없습니다.")
    public boolean isValidPeriod() {
        if (startDate == null || endDate == null) return true;
        return !endDate.isBefore(startDate);
    }
}
