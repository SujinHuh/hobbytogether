package com.hobbytogther.main;

import com.hobbytogther.account.AccountRepository;
import com.hobbytogther.account.AccountService;
import com.hobbytogther.account.SignUpForm;
import lombok.With;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class MainControllerTest {

    /**
     * @RequiredArgsConstructor 생성자 주입을 해줄 수 없다. spring이 주입을 해주지 못한다.
     * junit이 개입을 해서 다른 인스턴스를 넣으려고 시도해서 / 즉,@Autowired를 사용해야한다.
     */
    @Autowired
    MockMvc mockMvc;
    @Autowired
    AccountService accountService;
    @Autowired
    AccountRepository accountRepository;

    @BeforeEach
    void createAccount() {
        SignUpForm signUpForm = new SignUpForm();
        signUpForm.setNickname("sujin");
        signUpForm.setEmail("sujin@email.com");
        signUpForm.setPassword("12345678");
        accountService.processNewAccount(signUpForm);
    }

    /**
     * 매번 모든 테스트를 실행 할 때마다 닉네임이랑 , 패스워드가 중복되어서 들어간다.
     * 삭제를 해주도록 한다.
     */
    @AfterEach
    void afterEach() {
        accountRepository.deleteAll();
    }


    @DisplayName("이메일로 로그인 성공")
    @Test
    public void login_email() throws Exception {

        //given

        //when
        mockMvc.perform(post("/login")
                .param("username", "sujin@email.com")//springSecurity에서 username, password는 정해져있다.
                .param("password", "12345678")
                .with(csrf())) //TODO spring security 를 기본으로 사용하는 기본적으로 csrf 프로텍션이 활성화가 되어있다. csrf 토큰이 같이 전송이 되어야 한다.
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/")) //redirectedUrl root로감
                .andExpect(authenticated().withUsername("sujin"))//인증이 sujin이름으로 인증이 됨

        ;

        //then

    }


    @DisplayName("닉네임 로그인 성공")
    @Test
    public void login_nickname() throws Exception {

        //given

        //when
        mockMvc.perform(post("/login")
                .param("username", "sujin")//springSecurity에서 username, password는 정해져있다.
                .param("password", "12345678")
                .with(csrf())) //TODO spring security 를 기본으로 사용하는 기본적으로 csrf 프로텍션이 활성화가 되어있다. csrf 토큰이 같이 전송이 되어야 한다.
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/")) //redirectedUrl root로감
                .andExpect(authenticated().withUsername("sujin"))//인증이 sujin이름으로 인증이 됨

        ;
    }

    @WithMockUser // test 실행시 authentication 에 springsecurity에 User Type에 해당하는 principal로(username, password)로 로그인/로그아웃이 됨
    @DisplayName("로그인 실패")
    @Test
    public void login_fail() throws Exception {

        //given

        //when
        mockMvc.perform(post("/login")
                .param("username", "test")
                .param("password", "5522561")
                .with(csrf())) //TODO spring security 를 기본으로 사용하는 기본적으로 csrf 프로텍션이 활성화가 되어있다. csrf 토큰이 같이 전송이 되어야 한다.
                .andExpect(status().is3xxRedirection())  //TODO 성공적으로 redirect가 될 것이다.
                .andExpect(redirectedUrl("/login?error")) //로그인에 error로 redirect이 될것
                .andExpect(unauthenticated())
        ;

        //then
    }

    @WithMockUser
    @DisplayName("로그인 아웃")
    @Test
    public void logout() throws Exception {

        //given

        //when
        mockMvc.perform(post("/logout")
                .with(csrf())) //TODO spring security 를 기본으로 사용하는 기본적으로 csrf 프로텍션이 활성화가 되어있다. csrf 토큰이 같이 전송이 되어야 한다.
                .andExpect(status().is3xxRedirection())  //TODO 성공적으로 redirect가 될 것이다.
                .andExpect(redirectedUrl("/"))
                .andExpect(unauthenticated())
        ;

        //then
    }
}