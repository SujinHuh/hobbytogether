package com.hobbytogther.modules.hobby;

import com.hobbytogther.modules.account.Account;
import com.hobbytogther.modules.account.UserAccount;
import com.hobbytogther.modules.hobby.Hobby;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class HobbyTest {

    Hobby hobby;
    Account account;
    UserAccount userAccount;

    @BeforeEach
    void beforeEach() {
        hobby = new Hobby();
        account = new Account();
        account.setNickname("sujin");
        account.setPassword("123456");

        userAccount = new UserAccount(account);
    }

    @DisplayName("Hobby - 공개, 인원모집중,이미 멤버나 스터디 관리자가 아니라면 스터디 가입 가능")
    @Test
    public void isJoinable() throws Exception {

        //given
        hobby.setPublished(true);
        hobby.setRecruiting(true);
        //when
        assertTrue(hobby.isJoinable(userAccount));
        //then
    }

    @DisplayName("Hobby - 공개했고 인원 모집 중이더라도, Hobby 관리자는 Hobby 가입이 불필요하다. ")
    @Test
    public void isJoinable_maneger_false() throws Exception {

        //given
        hobby.setPublished(true);
        hobby.setRecruiting(true);
        //when
       hobby.addManager(account);

        //then
        assertFalse(hobby.isJoinable(userAccount));
    }

    @DisplayName("Hobby 공개했고 인원 모집 중이더라도, Hobby 멤버는 스터디 재가입이 불필요하다.")
    @Test
    public void isJoinalble_member_false() throws Exception {

        //given
        hobby.setPublished(true);
        hobby.setRecruiting(true);
        //when
        hobby.addMember(account);
        //then
        assertFalse(hobby.isJoinable(userAccount));
    }

    @DisplayName("Hobby 관리자인지 확인")
    @Test
    public void isHobbyManager() throws Exception {

        //given
        hobby.addManager(account);
        //when
        assertTrue(hobby.isManager(userAccount));

        //then

    }
    @DisplayName("Hobby 멤버인지 확인")
    @Test
    public void isHobbyMember() throws Exception {
        //given
        hobby.addMember(account);
        //when
        assertTrue(hobby.isMember(userAccount));
        //then

    }
    @DisplayName("Hobby 비공개거나 인원 모집 중이 아니면 Hobby 가입이 불가능하다.")
    @Test
    public void isJoinable_noRecruiting() throws Exception {

        //given
        hobby.setPublished(false);
        hobby.setPublished(false);

        //when

        assertFalse(hobby.isJoinable(userAccount));
        //then

    }
}