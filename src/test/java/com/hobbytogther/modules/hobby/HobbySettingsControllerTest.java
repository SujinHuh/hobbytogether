package com.hobbytogther.modules.hobby;

import com.hobbytogther.WithAccount;
import com.hobbytogther.infra.AbstractContainerBaseTest;
import com.hobbytogther.infra.MockMvcTest;
import com.hobbytogther.modules.account.Account;
import com.hobbytogther.modules.account.AccountFactory;
import com.hobbytogther.modules.account.AccountRepository;
import com.hobbytogther.modules.hobby.validator.HobbyRepository;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;


@Transactional
@SpringBootTest
@AutoConfigureMockMvc
//@MockMvcTest
public class HobbySettingsControllerTest{

    @Autowired
    MockMvc mockMvc;
    @Autowired
    HobbyFactory hobbyFactory;
    @Autowired
    AccountFactory accountFactory;
    @Autowired
    AccountRepository accountRepository;
    @Autowired
    HobbyRepository hobbyRepository;

    @Test
    @WithAccount("sujin")
    @DisplayName("Hobby 소개 수정 폼 조회 - 실패 (권한 없는 유저)")
    void updateDescriptionForm_fail() throws Exception {
        Account HuhSu = accountFactory.createAccount("HuhSu");
        Hobby hobby = hobbyFactory.createHobby("test-hobby", HuhSu);

        mockMvc.perform(get("/hobby/" + hobby.getPath() + "/settings/description"))
                .andExpect(status().isOk())
                .andExpect(view().name("error"));
    }

    @Test
    @WithAccount("sujin")
    @DisplayName("Hobby 소개 수정 폼 조회 - 성공")
    void updateDescriptionForm_success() throws Exception {
        Account sujin = accountRepository.findByNickname("sujin");
        Hobby hobby = hobbyFactory.createHobby("test-hobby", sujin);

        mockMvc.perform(get("/hobby/" + hobby.getPath() + "/settings/description"))
                .andExpect(status().isOk())
                .andExpect(view().name("hobby/settings/description"))
                .andExpect(model().attributeExists("hobbyDescriptionForm"))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("hobby"));
    }

    @Test
    @WithAccount("sujin")
    @DisplayName("Hobby 소개 수정 - 성공")
    void updateDescription_success() throws Exception {
        Account sujin = accountRepository.findByNickname("sujin");
        Hobby hobby = hobbyFactory.createHobby("test-hobby", sujin);

        String settingsDescriptionUrl = "/hobby/" + hobby.getPath() + "/settings/description";
        mockMvc.perform(post(settingsDescriptionUrl)
                .param("shortDescription", "short description")
                .param("fullDescription", "full description")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(settingsDescriptionUrl))
                .andExpect(flash().attributeExists("message"));
    }

    @Test
    @WithAccount("sujin")
    @DisplayName("Hobby 소개 수정 - 실패")
    void updateDescription_fail() throws Exception {
        Account sujin = accountRepository.findByNickname("sujin");
        Hobby hobby = hobbyFactory.createHobby("test-hobby", sujin);

        String settingsDescriptionUrl = "/hobby/" + hobby.getPath() + "/settings/description";
        mockMvc.perform(post(settingsDescriptionUrl)
                .param("shortDescription", "")
                .param("fullDescription", "full description")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(model().hasErrors())
                .andExpect(model().attributeExists("hobbyDescriptionForm"))
                .andExpect(model().attributeExists("hobby"))
                .andExpect(model().attributeExists("account"));
    }

}
