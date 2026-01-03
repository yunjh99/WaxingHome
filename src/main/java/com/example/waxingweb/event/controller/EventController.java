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
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequiredArgsConstructor
@RequestMapping("/community/event")
public class EventController {

    private final EventService eventService;


    // 진행중인 이벤트
    @GetMapping("/list")
    public String list(Model model,
                       @PageableDefault(size = 8) Pageable pageable) {

        Page<EventListDto> events = eventService.getActiveEvents(pageable);
        model.addAttribute("events", events);

        return "pages/community/event/list";
    }

    // 글 작성 & 수정 페이지
    @GetMapping("/form")
    public String eventForm() { return "pages/community/event/form"; }



    // 종료된 이벤트
    @GetMapping("Expired")
    public String getEndedEvents(Model model,
                       @PageableDefault(size=8) Pageable pageable) {

        Page<Event> events = eventService.getExpiredEvents(pageable);
        model.addAttribute("events", events);

        return "pages/community/event";
    }

    @PostMapping
    public String create(
            @Valid EventCreateRequest request,
            BindingResult bindingResult,
            @RequestParam(required = false) MultipartFile thumbnail,
            @RequestParam(required = false) MultipartFile bodyImage
    ) {
        if (bindingResult.hasErrors()) {
            return "pages/community/event-form";
        }
        return "redirect:/event";
    }


}
