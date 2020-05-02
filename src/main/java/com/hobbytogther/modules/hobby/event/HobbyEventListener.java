package com.hobbytogther.modules.hobby.event;

import com.hobbytogther.modules.hobby.Hobby;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Async
@Component
@Transactional(readOnly = true)
public class HobbyEventListener {


    @EventListener /** 이밴트 처리 */
    public void handleHobbyCreatedEvent(HobbyCreateEvent hobbyCreateEvent) {
        Hobby hobby = hobbyCreateEvent.getHobby();
        log.info(hobby.getTitle() + "is created");
        // TODO 이메일 전송, DB에 알림 정보를 저장하면됨
        throw new RuntimeException();
    }
}
