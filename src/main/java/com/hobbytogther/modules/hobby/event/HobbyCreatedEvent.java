package com.hobbytogther.modules.hobby.event;

import com.hobbytogther.modules.hobby.Hobby;
import lombok.Data;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class HobbyCreatedEvent {

    private Hobby hobby;

    public HobbyCreatedEvent(Hobby hobby) {
        this.hobby = hobby;
    }
}
