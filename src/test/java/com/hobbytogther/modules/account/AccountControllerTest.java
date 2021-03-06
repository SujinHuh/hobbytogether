package com.hobbytogther.modules.account;

import com.hobbytogther.infra.AbstractContainerBaseTest;
import com.hobbytogther.infra.MockMvcTest;
import com.hobbytogther.infra.mail.EmailMessage;
import com.hobbytogther.infra.mail.EmailService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestComponent;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.then;

import static org.mockito.Mockito.mock;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
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
    EmailService emailService;

    @DisplayName("회원원가입 인증 메일 - 입력값 오류")
    @Test
    public void checkEmailToken_ErrorInPut() throws Exception {
        /**
         * - 입력 값이 잘못 된
         *      1. error 프로퍼티가 model에 들어있는지 확인
         *      2, 뷰 이름이 account/checked-email 인지 확인
         */
        mockMvc.perform(get("/check-email-token")
                .param("token", "aldkfjed")
                .param("email", "Test@email.con"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("error"))
                .andExpect(view().name("account/checked-email"))
                .andExpect(unauthenticated())//인증 메일이 안된 사용자
        ;
    }


    @DisplayName("회원원가입 인증 메일 - 입력값 정상")
    @Test
    public void checkEmailToken_CorrectInPut() throws Exception {
        /**
         * - 입력 값이 올바른
         *   1. 모델에 error가 없는지 확인
         *   2. 모델에 numberOuUser가 있는지 확인
         *   3. 모델에 nickname이 있는지 확인
         *   4. 뷰 이름 확인하기
         */
        Account account = Account.builder()
                .email("tetst@mail.com")
                .password("12345dac")
                .nickname("sujin")
                .build();

        Account newAccount = accountRepository.save(account);
        newAccount.generateEmailCheckToken();

        mockMvc.perform(get("/check-email-token")
                .param("token", newAccount.getEmailCheckToken())
                .param("email", newAccount.getEmail()))
                .andExpect(model().attributeExists("numberOfUser"))
                .andExpect(status().isOk())
                .andExpect(model().attributeDoesNotExist("error"))
                .andExpect(model().attributeExists("nickname"))
                .andExpect(model().attributeExists("numberOfUser"))
                .andExpect(view().name("account/checked-email"))
                .andExpect(authenticated().withUsername("sujin"));//sujin이름으로 인증 메일이 확인이 된 상태
        ;

    }


    @DisplayName("회원원가입 화면 정상화면 보이는지 확인")
    @Test
    public void signUpFormCheck() throws Exception {

        //given
        mockMvc.perform(get("/sign-up"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("account/sign-up"))
                .andExpect(model().attributeExists("signUpForm"))
                .andExpect(unauthenticated())
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
                .andExpect(view().name("account/sign-up"))
                .andExpect(unauthenticated())
        ;
        //when

        //then

    }


    @DisplayName("회원 가입 처리_ 입력값 정상")
    @Test
    public void signUpSubmit_CorrectInPut() throws Exception {
        //given
        mockMvc.perform(post("/sign-up")
                .param("nickname", "sujin")
                .param("email", "test@naver.com")
                .param("password", "1231")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/"))
                .andExpect(authenticated().withUsername("sujin"))
        ;

        //when
        Account account = accountRepository.findByEmail("test@naver.com");
        assertNotNull(account);
        assertNotEquals(account.getPassword(), "1231");
        assertNotEquals(account, "test@naver.com");
        //then
        then(emailService).should().sendEmail(any(EmailMessage.class)); //아무런 인스턴스 타입으로 Send 호출이 되었는가 확인
    }
}