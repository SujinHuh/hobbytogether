package com.hobbytogther.hobby;

import com.hobbytogther.account.CurrentAccount;
import com.hobbytogther.domain.Account;
import com.hobbytogther.domain.Hobby;
import com.hobbytogther.hobby.form.HobbyForm;
import com.hobbytogther.hobby.validator.HobbyFormValidator;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;


@Controller
@RequiredArgsConstructor
public class HobbyController {

    private final HobbyService hobbyService;
    private final ModelMapper modelMapper;
    private final HobbyFormValidator hobbyFormValidator;

    @InitBinder("hobbyForm")
    public void studyFormInitBinder(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(hobbyFormValidator);
    }
    @GetMapping("/new-hobby")
    public String newHobbyForm(@CurrentAccount Account account, Model model) {
        model.addAttribute(account);
        model.addAttribute(new HobbyForm());
        return "hobby/form";
    }

    @PostMapping("/new-hobby")
    public String newHobbySubmit(@CurrentAccount Account account, @Valid HobbyForm hobbyForm, Errors errors) {
        if (errors.hasErrors()) {
            return "hobby/form";
        }

        Hobby newStudy = hobbyService.createNewHobby(modelMapper.map(hobbyForm, Hobby.class), account);
        return "redirect:/hobby/" + URLEncoder.encode(newStudy.getPath(), StandardCharsets.UTF_8);
    }

}