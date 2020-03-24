package com.hobbytogther.account;

import com.hobbytogther.domain.Account;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.then;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Transactional
@SpringBootTest
@AutoConfigureMockMvc
class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AccountRepository accountRepository;

    @MockBean
    JavaMailSender javaMailSender;

    @DisplayName("회원원가입 화면 정상화면 보이는지 확인")
    @Test
    public void signUpFormCheck() throws Exception {

        //given
        mockMvc.perform(get("/sign-up"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("account/sign-up"))
                .andExpect(model().attributeExists("signUpForm"))
        ;
        //when

        //then

    }

    @DisplayName("회원 가입 처리_ 입력값 오류")
    @Test
    public void signUpSubmit_ErrorInPut() throws Exception {

        //given
        mockMvc.perform(post("/sign-up")
                .param("nickname", "sujin")
                .param("email", "email...")
                .param("password", "1231")
                .with(csrf())) //TODO csrf 토큰을 넣어줘야 함 **Form을 보내야 할 때는 csrf를 넣어줘야 함 *
                .andExpect(status().isOk())
                .andExpect(view().name("account/sign-up"));
        //when

        //then

    }


    @DisplayName("회원 가입 처리_ 입력값 정상")
    @Test
    public void signUpSubmit_CorrectInPut() throws Exception {

        //given

        /**
         * 1. 회원 정보 저장
         * 2. 인증 이메일 발송
         * 3. 처리 후 첫 페이지로 이동
         */
        mockMvc.perform(post("/sign-up")
                .param("nickname", "sujin")
                .param("email", "test@naver.com")
                .param("password", "1231")
                .with(csrf())) //TODO csrf 토큰을 넣어줘야 함 **Form을 보내야 할 때는 csrf를 넣어줘야 함 *
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/"));
        //when
        assertTrue(accountRepository.existsByEmail("test@naver.com"));
        //then
        then(javaMailSender).should().send(any(SimpleMailMessage.class)); //아무런 인스턴스 타입으로 Send 호출이 되었는가 확인
    }
}