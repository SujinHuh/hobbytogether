package com.hobbytogther.hobby;

import com.hobbytogther.domain.Account;
import com.hobbytogther.domain.Hobby;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class HobbyService {

    private final HobbyRepository repository;

    public Hobby createNewHobby(Hobby hobby, Account account) {
        Hobby newHobby = repository.save(hobby);
        newHobby.addManager(account);
        return newHobby;
    }
}
