package com.hobbytogther.modules.main;

import com.hobbytogther.modules.account.CurrentAccount;
import com.hobbytogther.modules.account.Account;
import com.hobbytogther.modules.notification.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {

    @GetMapping("/")
    public String home(@CurrentAccount Account account, Model model){
        if(account != null) { //인증을 한 사용자
            model.addAttribute(account);
        }
        //view 에서는 null인지 아닌지 확인
        return "index";
    }
    /**
     * login 처리
     */
    @GetMapping("/login")
    public String login() {
        return "login";
    }
}

