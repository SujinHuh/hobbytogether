package com.hobbytogther.modules.hobby;

import com.hobbytogther.WithAccount;
import com.hobbytogther.modules.account.Account;
import com.hobbytogther.modules.account.AccountRepository;
import com.hobbytogther.modules.account.UserAccount;
import com.hobbytogther.modules.hobby.validator.HobbyRepository;
import com.hobbytogther.modules.hobby.validator.HobbyService;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Transactional
@SpringBootTest
@AutoConfigureMockMvc
@RequiredArgsConstructor
class HobbyControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    HobbyRepository hobbyRepository;

    @Autowired
    AccountRepository accountRepository;
    @Autowired
    HobbyService hobbyService;

    UserAccount userAccount;

    @AfterEach
    void afterEach() {
        accountRepository.deleteAll();
    }

    @Test
    @WithAccount("sujin")
    @DisplayName("Hobby 개설 조회")
    public void createHobbyForm() throws Exception {
        mockMvc.perform(get("/new-hobby"))
                .andExpect(status().isOk())
                .andExpect(view().name("hobby/form"))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("hobbyForm"))
        ;
    }


    @Test
    @WithAccount("sujin")
    @DisplayName("Hobby 개설 - 완료")
    void createHobby_success() throws Exception {
        mockMvc.perform(post("/new-hobby")
                .param("path", "test-path")
                .param("title", "hobby title")
                .param("shortDescription", "short description of a hobby")
                .param("fullDescription", "full description of a hobby")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/hobby/test-path"));

        Hobby hobby = hobbyRepository.findByPath("test-path");
        assertNotNull(hobby);
        Account account = accountRepository.findByNickname("sujin");
        assertTrue(hobby.getManagers().contains(account));
    }




    @Test
    @WithAccount("sujin")
    @DisplayName("hobby 개설 실패")
    public void createHobby_fail() throws Exception {
        mockMvc.perform(post("/new-hobby")
                .param("path","wrong Path")
                .param("title","Hobby")
                .param("shortDescription", "short description hobby")
                .param("fullDescription", "full description  hobby")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("hobby/form"))
                .andExpect(model().hasErrors())
                .andExpect(model().attributeExists("hobbyForm"))
                .andExpect(model().attributeExists("account"))

        ;

        Hobby hobby = hobbyRepository.findByPath("test-path");
        assertNull(hobby);

    }

    @Test
    @WithAccount("sujin")
    @DisplayName("Hobby 조회")
    void viewHobby() throws Exception {
        Hobby hobby = new Hobby();
        hobby.setPath("test-path");
        hobby.setTitle("test hobby");
        hobby.setShortDescription("short description");
        hobby.setFullDescription("<p>full description</p>");

        Account sujin = accountRepository.findByNickname("sujin");
        hobbyService.createNewHobby(hobby, sujin);

        mockMvc.perform(get("/hobby/test-path"))
                .andExpect(view().name("hobby/view"))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("hobby"));
    }

    @Test
    @WithAccount("sujin")
    @DisplayName("Hobby 가입")
    void hobbyJoin() throws Exception {

        Hobby hobby = new Hobby();
        hobby.setPath("test-path");
        hobby.setTitle("test hobby");
        hobby.setShortDescription("short description");
        hobby.setFullDescription("<p>full description</p>");

        Account account = accountRepository.findByNickname("sujin");

        assertNotNull(hobby);
        hobbyService.addMember(hobby,account);
        assertTrue(hobby.getMembers().contains(account));
    }

    @Test
    @WithAccount("sujin")
    @DisplayName("Hobby 탈퇴")
    void hobbyleave() throws Exception {

        Hobby hobby = new Hobby();
        hobby.setPath("test-path");
        hobby.setTitle("test hobby");
        hobby.setShortDescription("short description");
        hobby.setFullDescription("<p>full description</p>");

        Account sujin = accountRepository.findByNickname("sujin");

        assertNotNull(hobby);
        hobbyService.removeMember(hobby,sujin);

        assertFalse(hobby.getMembers().contains(sujin));
    }

}