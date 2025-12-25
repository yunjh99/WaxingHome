package com.example.waxingweb.event.service;

import com.example.waxingweb.event.domain.Event;
import com.example.waxingweb.event.dto.EventCreateRequest;
import com.example.waxingweb.event.repository.EventRepository;
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

    public Page<Event> getActiveEvents(Pageable pageable) {
        LocalDate today = LocalDate.now();
        return eventRepository.findAllActiveEvents(today, pageable);
    }

    public Page<Event> getExpiredEvents(Pageable pageable) {
        LocalDate today = LocalDate.now();
        return eventRepository.findAllExpiredEvents(today, pageable);
    }



}
