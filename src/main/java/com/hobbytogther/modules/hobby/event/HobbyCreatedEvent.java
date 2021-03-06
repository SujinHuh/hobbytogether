package com.hobbytogther.modules.hobby.event;

import com.hobbytogther.modules.hobby.Hobby;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEvent;

@Getter
@RequiredArgsConstructor
public class HobbyCreatedEvent {

    private final Hobby hobby;

}
