package com.hobbytogther.modules.hobby;

import com.hobbytogther.modules.account.Account;
import com.hobbytogther.modules.hobby.validator.HobbyRepository;
import com.hobbytogther.modules.hobby.validator.HobbyService;
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
