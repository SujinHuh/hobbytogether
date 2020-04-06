package com.hobbytogther.settings;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hobbytogther.WithAccount;
import com.hobbytogther.account.AccountRepository;
import com.hobbytogther.account.AccountService;
import com.hobbytogther.domain.Account;
import com.hobbytogther.domain.Tag;
import com.hobbytogther.settings.form.TagForm;
import com.hobbytogther.tag.TagRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static com.hobbytogther.settings.SettingsController.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;

/**
 * Test를 하나의 큰 단위로 보고 controller 부터 하나의 기능 단위로 보는것
 * Service ~ Repository 까지 테스트를 하는것
 * 다양한 수를 고려해서 해야 한다.
 * 단위 테스트를
 */
@Transactional
@SpringBootTest
@AutoConfigureMockMvc
class SettingsControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    AccountService accountService;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    TagRepository tagRepository;

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
        mockMvc.perform(get(ROOT + SETTINGS + PASSWORD))
                .andExpect(status().isOk()) // isOk로 폼이 나와야 한다.
                .andExpect(model().attributeExists("account")) //폼에 모델이 어트리뷰트가 있어야 하고
                .andExpect(model().attributeExists("passwordForm")) //프로파일이 있어야 한다.
        ;

        /** URL 자체가 인증된 사용자만 접근이 가능한것  @WithAccount("sujin") 없으면 동작하지 않는다.*/
    }

    @WithAccount("sujin")
    @DisplayName("프로필 수정 - 입력값 정상")
    @Test
    public void updateProfile() throws Exception {
        String bio = "짧은 자기 소개를 하겠습니다.";
        mockMvc.perform(post(ROOT + SETTINGS + PROFILE)
                .param("bio", bio)
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(ROOT + SETTINGS + PROFILE))
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
        mockMvc.perform(post(ROOT + SETTINGS + PROFILE)
                .param("bio", bio)
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name(SETTINGS + PROFILE))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("profile"))

                .andExpect(model().hasErrors())
        ;

        Account sujin = accountRepository.findByNickname("sujin");
        assertNull(sujin.getBio());// 데이터가 변경되지 않았을것
    }

    @WithAccount("sujin")
    @DisplayName("패스워드 수정 폼")
    @Test
    public void updatePassword() throws Exception {

        mockMvc.perform(get(ROOT + SETTINGS + PASSWORD))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("passwordForm"))
        ;
    }


    @WithAccount("sujin")
    @DisplayName("패스워드 수정 - 입력값 정상")
    @Test
    void updatePassword_success() throws Exception {
        mockMvc.perform(post(ROOT + SETTINGS + PASSWORD)
                .param("newPassword", "12345678")
                .param("newPasswordConfirm", "12345678")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(ROOT + SETTINGS + PASSWORD))
                .andExpect(flash().attributeExists("message"));

        Account sujin = accountRepository.findByNickname("sujin");
        assertTrue(passwordEncoder.matches("12345678", sujin.getPassword()));
    }

    @WithAccount("sujin")
    @DisplayName("패스워드 수정 - 입력값 에러 - 패스워드 불일치")
    @Test
    void updatePassword_fail() throws Exception {
        mockMvc.perform(post(ROOT + SETTINGS + PASSWORD)
                .param("newPassword", "12345678")
                .param("newPasswordConfirm", "11111111")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name(SETTINGS + PASSWORD))
                .andExpect(model().hasErrors())
                .andExpect(model().hasErrors())
                .andExpect(model().attributeExists("passwordForm"))
                .andExpect(model().attributeExists("account"))

        ;
    }

    /** Tag Test */

    @WithAccount("sujin")
    @DisplayName("계정 태그 수정 폼")
    @Test
    void updateTagsFrom() throws Exception {
        /** (이전과 다른 Test 작성) data를 보내는게 다르다. 이전에는 form에 보내는 것 form에 들어가는 데이터를 param으로 채움 */
        mockMvc.perform(get(ROOT + SETTINGS + TAGS))
                .andExpect(view().name(SETTINGS + TAGS))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("whitelist"))
                .andExpect(model().attributeExists("tags"))
                ;
    }

    @WithAccount("sujin")
    @DisplayName("계정에 태그 추가")
    @Test
    void addTag() throws Exception {

        /** JSON 문자열 - 아래 객체를 JSON으로 변환한 형태의 문자열 */
        TagForm tagForm = new TagForm();
        tagForm.setTagTitle("newTag");

        mockMvc.perform(post(ROOT + SETTINGS + TAGS + "/add")
                .contentType(MediaType.APPLICATION_JSON) /**요청안에 param으로 들어오는 것 아니라 '본문'으로 들어온다. '본문'의 TYPE JSON으로 들어온다.*/
                .content(objectMapper.writeValueAsBytes(tagForm)) /**JSON문자 열로 들어온다. **ObjectMapper로 객체를 JSON으로 변환 가능 */
                .with(csrf()))/**Post요청 할 때는 반드시!! csrf */
                .andExpect(status().isOk())
        ;

        /** tag가 실제로 저장이 되었는지 확인 */
        Tag newTag = tagRepository.findByTitle("newTag");
        assertNotNull(newTag);
        Account sujin = accountRepository.findByNickname("sujin"); /** 리파지토지토리에서 꺼내온것까지 트랜잭션 왜? 리파지토리 통해서 가져왔으니까 */
        /** 가져온 sujin 상태는? persistent 상태가 아니다. 'detached'상태이다. persistent 상태가 되려면 '전부(클래스)'가 트랜젝션이어야 한다. */
        /** detached 상태에다가 "추가적으로 정보를가져 올 수 없다." 즉, 정보를 가져오려면, persistent 상태여야한다. */
        /** persistent 상태를 만드려면 @Transactional추가 해야한다.*/
        assertTrue(sujin.getTags().contains(newTag)); /** getTags가 계정안에 들어있느냐 /assertTrue 사실인가 */
        // getTags : Lazy로딩
    }

    @WithAccount("sujin")
    @DisplayName("계정에 태그 삭제")
    @Test
    void removeTag() throws Exception {
        Account sujin = accountRepository.findByNickname("sujin");
        Tag newTage = tagRepository.save(Tag.builder().title("newTag").build());
        accountService.addTag(sujin, newTage);

        assertTrue(sujin.getTags().contains(newTage));

        TagForm tagForm = new TagForm();
        tagForm.setTagTitle("newTag");

        mockMvc.perform(post(ROOT + SETTINGS + TAGS + "/remove")
                .contentType(MediaType.APPLICATION_JSON) /**요청안에 param으로 들어오는 것 아니라 '본문'으로 들어온다. '본문'의 TYPE JSON으로 들어온다. */
                .content(objectMapper.writeValueAsBytes(tagForm)) /**JSON문자 열로 들어온다. **ObjectMapper로 객체를 JSON으로 변환 가능 */
                .with(csrf()))/**Post요청 할 때는 반드시!! csrf */
                .andExpect(status().isOk())
        ;
        assertFalse(sujin.getTags().contains(newTage));


    }
}