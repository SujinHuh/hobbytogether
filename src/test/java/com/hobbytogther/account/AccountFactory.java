package com.hobbytogther.account;

import com.hobbytogther.domain.Account;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AccountFactory {

    @Autowired
    AccountRepository accountRepository;

    public Account createAccount(String nickname) {
        Account sujin = new Account();
        sujin.setNickname(nickname);
        sujin.setEmail(nickname + "@email.com");
        accountRepository.save(sujin);
        return sujin;
    }

}
