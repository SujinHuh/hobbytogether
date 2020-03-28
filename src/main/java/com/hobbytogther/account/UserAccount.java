package com.hobbytogther.account;

import com.hobbytogther.domain.Account;
import lombok.Getter;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.List;

/**
 * Account를 들고 있는 중간 역활을 해줄 수 있는 ->UserAccount
 * SpringSecurity가 다루는 User 정보 , domain에서 다루는 User 정보에 사이의 Gap을 매꿔주는 "어댑터 역활"
 * User -> SpringSecurity에서 오는 것
 * UserAccount : principal 객체로 사₩
 */
@Getter
public class UserAccount extends User {

    private Account account;

    public UserAccount(Account account) {
        super(account.getNickname(), account.getPassword(), List.of(new SimpleGrantedAuthority("ROLE_USER")));
        this.account = account;
    }

}

