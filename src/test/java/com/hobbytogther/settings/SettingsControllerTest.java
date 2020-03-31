package com.hobbytogther.settings;

import com.hobbytogther.WithAccount;
import com.hobbytogther.account.AccountRepository;
import com.hobbytogther.account.AccountService;
import com.hobbytogther.domain.Account;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;

@SpringBootTest
@AutoConfigureMockMvc
class SettingsControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    AccountService accountService;

    @AfterEach
    void afterEach() {
        accountRepository.deleteAll();
    }
    /** @WithAccount 커스텀한 에노테이션
     * @withAccount를 사용할 때 마다 account에 해당하는 계정을 만들어 넣어 주기 때문에  @AfterEach를 사용해서 매번 지워주면 된다.*/

    @WithAccount("sujin")
    @DisplayName("프로필 수정 - 폼")
    @Test
    public void updateProfile_Form() throws Exception {
        mockMvc.perform(get(SettingsController.SETTINGS_PROFILE_URL))
                .andExpect(status().isOk()) // isOk로 폼이 나와야 한다.
                .andExpect(model().attributeExists("account")) //폼에 모델이 어트리뷰트가 있어야 하고
                .andExpect(model().attributeExists("profile")) //프로파일이 있어야 한다.
        ;

        /** URL 자체가 인증된 사용자만 접근이 가능한것  @WithAccount("sujin") 없으면 동작하지 않는다.*/
    }

    @WithAccount("sujin")
    @DisplayName("프로필 수정 - 입력값 정상")
    @Test
    public void updateProfile() throws Exception {
        String bio = "짧은 자기 소개를 하겠습니다.";
        mockMvc.perform(post(SettingsController.SETTINGS_PROFILE_URL)
                .param("bio", bio)
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(SettingsController.SETTINGS_PROFILE_URL))
                .andExpect(flash().attributeExists("message"))
        ;

        Account sujin = accountRepository.findByNickname("sujin");
        assertEquals(bio, sujin.getBio());
    }

    @WithAccount("sujin")
    @DisplayName("프로필 수정 - 입력값 오류")
    @Test
    public void updateProfile_error() throws Exception {
        String bio = "긴~~~긴 자기 소개를 하겠습니다.긴~~~긴 자기 소개를 하겠습니.긴~~~긴 자기 소개를 하겠습니다.긴~~~긴 자기 소개를 하겠습니다.긴~~~긴 자기 소개를 하겠습니다.";
        mockMvc.perform(post(SettingsController.SETTINGS_PROFILE_URL)
                .param("bio", bio)
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name(SettingsController.SETTINGS_PROFILE_VIEW_NAME))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("profile"))
                .andExpect(model().hasErrors())
        ;

        Account sujin = accountRepository.findByNickname("sujin");
        assertNull(sujin.getBio());// 데이터가 변경되지 않았을것
    }

}