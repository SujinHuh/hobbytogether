package com.hobbytogther.settings;

import com.hobbytogther.account.AccountService;
import com.hobbytogther.account.CurrentUser;
import com.hobbytogther.domain.Account;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;

@Controller
@RequiredArgsConstructor
public class SettingsController {

    static final String SETTINGS_PROFILE_VIEW_NAME = "settings/profile";
    static final String SETTINGS_PROFILE_URL = "/settings/profile";

    private final AccountService accountService;

    /**
     * 현재 사용자 정보 넣어줌
     */
    @GetMapping(SETTINGS_PROFILE_URL)
    public String updateProfileForm(@CurrentUser Account account, Model model) {
        model.addAttribute(account);
        model.addAttribute(new Profile(account));
        return SETTINGS_PROFILE_VIEW_NAME;
    }

    /**현재 사용자 정보 수정*/
    @PostMapping(SETTINGS_PROFILE_URL)
    //errors가 (ModelAttribute)바인딩 에러를 받아주는 폼 애트리 뷰트 객체 오른쪽에 두어야 한다. (ModelAttribute)생략 가능
    public String updateProfile(@CurrentUser Account account, @Valid @ModelAttribute Profile profile, Errors errors,
                                Model model, RedirectAttributes attributes) {
        if (errors.hasErrors()) {
            model.addAttribute(account);
            return SETTINGS_PROFILE_VIEW_NAME;
        }
        accountService.updateProfile(account,profile);
        //잠깐 한번쓸 데이터 RedirectAttributes 한번 사용하고 없어짐
        attributes.addFlashAttribute("message", "프로필 수정 완료");
        return "redirect:" + SETTINGS_PROFILE_URL;
    }
}
