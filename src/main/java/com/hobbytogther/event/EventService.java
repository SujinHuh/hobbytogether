package com.hobbytogther.event;

import com.hobbytogther.domain.Account;
import com.hobbytogther.domain.Event;
import com.hobbytogther.domain.Hobby;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Transactional
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;

    public Event createEvent(Event event, Hobby hobby, Account account) {
        event.setCreatedBy(account);
        event.setCreatedDateTime(LocalDateTime.now());
        event.setHobby(hobby);

        return eventRepository.save(event);
    }
}
