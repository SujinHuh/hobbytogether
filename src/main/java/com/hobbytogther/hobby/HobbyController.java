package com.hobbytogther.hobby;

import com.hobbytogther.account.CurrentAccount;
import com.hobbytogther.domain.Account;
import com.hobbytogther.hobby.form.HobbyForm;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HobbyController {


    @GetMapping("/new-hobby")
    public String newStudyForm(@CurrentAccount Account account , Model model) {
        model.addAttribute(account);
        model.addAttribute(new HobbyForm());
        return "hobby/form";
    }
}
