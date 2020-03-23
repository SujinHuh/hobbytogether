package com.hobbytogther.account;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;

@Controller
public class AccountController {

    @GetMapping("/sign-up")
    public String signUpFrom(Model model) {
        model.addAttribute("signUpForm",new SignUpForm());
        return "account/sign-up";
    }

    @PostMapping("/sign-up")
    public String signUpSubmit(@Valid SignUpForm signUpForm, BindingResult errors) {
        if(errors.hasErrors()) {
            return "account/sign-up";
        }
        // TODO 회원 가입 처리
        return "redirect:/";
    }

}
