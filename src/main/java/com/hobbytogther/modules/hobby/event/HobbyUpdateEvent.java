package com.hobbytogther.modules.hobby.event;
import com.hobbytogther.modules.hobby.Hobby;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class HobbyUpdateEvent {

    private final Hobby hobby;

    private final String message;
}
