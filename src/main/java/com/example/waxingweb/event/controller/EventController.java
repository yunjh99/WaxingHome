package com.example.waxingweb.event.controller;

import com.example.waxingweb.event.domain.Event;
import com.example.waxingweb.event.dto.EventCreateRequest;
import com.example.waxingweb.event.service.EventService;
import com.example.waxingweb.file.service.FileService;
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

import java.time.LocalDate;

@Controller
@RequiredArgsConstructor
@RequestMapping("event")
public class EventController {

    private final EventService eventService;
    private final FileService fileService;

    // 진행중인 이벤트
    @GetMapping
    public String getActiveEvents(Model model,
                       @PageableDefault(size=8) Pageable pageable) {

        Page<Event> events = eventService.getActiveEvents(pageable);
        model.addAttribute("events", events);

        return "pages/community/event";
    }

    // 종료된 이벤트
    @GetMapping("ended")
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
