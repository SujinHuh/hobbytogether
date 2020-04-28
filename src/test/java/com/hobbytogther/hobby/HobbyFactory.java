package com.hobbytogther.hobby;

import com.hobbytogther.domain.Account;
import com.hobbytogther.domain.Hobby;
import com.hobbytogther.hobby.validator.HobbyRepository;
import com.hobbytogther.hobby.validator.HobbyService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class HobbyFactory {

    @Autowired
    HobbyService hobbyService;
    @Autowired
    HobbyRepository hobbyRepository;

    public Hobby createHobby(String path, Account manager) {
        Hobby hobby = new Hobby();
        hobby.setPath(path);
        hobbyService.createNewHobby(hobby, manager);
        return hobby;
    }

}
