package com.example.waxingweb.event.controller;

import com.example.waxingweb.event.domain.Event;
import com.example.waxingweb.event.dto.EventCreateRequest;
import com.example.waxingweb.event.dto.EventListDto;
import com.example.waxingweb.event.service.EventService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;


@Controller
@RequiredArgsConstructor
@RequestMapping("/community/events")
public class EventController {

    private final EventService eventService;


    // 진행중인 이벤트
    @GetMapping
    public String list(Model model,
                       @PageableDefault(size = 8) Pageable pageable) {

        Page<EventListDto> events = eventService.getActiveEvents(pageable);
        model.addAttribute("events", events);

        return "pages/community/event/list";
    }


    // 종료된 이벤트
    @GetMapping("/expired")
    public String getEndedEvents(Model model,
                                 @PageableDefault(size=8) Pageable pageable) {

        Page<Event> events = eventService.getExpiredEvents(pageable);
        model.addAttribute("events", events);

        return "pages/community/event";

    }

    // 이벤트 생성
    @PostMapping
    public String create(
            @Valid  @ModelAttribute EventCreateRequest request,
            BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "pages/community/event-form";
        }

        // 이벤트를 생성하고, event ID를 반환받음
        Long eventId = eventService.create(null, request);

        // 이벤트 생성 후 해당 게시글 상세로 리다이렉트
        return "redirect:/community/events/" + eventId;
    }


    // 이벤트 작성 & 수정 페이지
    @GetMapping("/form")
    public String eventForm() { return "pages/community/event/form"; }


}
