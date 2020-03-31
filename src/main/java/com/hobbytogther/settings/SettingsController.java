package com.hobbytogther.settings;

import com.hobbytogther.account.CurrentUser;
import com.hobbytogther.domain.Account;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SettingsController {

    static final String SETTINGS_PROFILE_VIEW_NAME = "settings/profile";
    static final String SETTINGS_PROFILE_URL = "/settings/profile";


    /**
     * 현재 사용자 정보 넣어줌
     */
    @GetMapping(SETTINGS_PROFILE_URL)
    public String updateProfileForm(@CurrentUser Account account, Model model) {
        model.addAttribute(account);
        model.addAttribute(new Profile(account));
        return SETTINGS_PROFILE_VIEW_NAME;
    }
}
