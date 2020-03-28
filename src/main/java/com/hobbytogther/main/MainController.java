package com.hobbytogther.main;

import com.hobbytogther.account.CurrentUser;
import com.hobbytogther.domain.Account;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {

    @GetMapping("/")
    public String home(@CurrentUser Account account, Model model){
        if(account != null) { //인증을 한 사용자
            model.addAttribute(account);
        }
        //view 에서는 null인지 아닌지 확인
        return "index";
    }

}

