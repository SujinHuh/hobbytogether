package com.hobbytogther.modules.hobby.event;

import com.hobbytogther.modules.hobby.Hobby;
import lombok.Data;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class HobbyCreateEvent {

    private Hobby hobby;

    public HobbyCreateEvent(Hobby hobby) {
        this.hobby = hobby;
    }
}
