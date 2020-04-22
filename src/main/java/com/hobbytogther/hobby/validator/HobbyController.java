package com.hobbytogther.hobby.validator;

import com.hobbytogther.account.CurrentAccount;
import com.hobbytogther.domain.Account;
import com.hobbytogther.domain.Hobby;
import com.hobbytogther.hobby.form.HobbyForm;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
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
    private final HobbyRepository hobbyRepository;

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
    public String newHobbySubmit(@CurrentAccount Account account, @Valid HobbyForm hobbyForm, Errors errors ,Model model) {
        if (errors.hasErrors()) {
            model.addAttribute(account);
            return "hobby/form";
        }

        Hobby newStudy = hobbyService.createNewHobby(modelMapper.map(hobbyForm, Hobby.class), account);
        return "redirect:/hobby/" + URLEncoder.encode(newStudy.getPath(), StandardCharsets.UTF_8);
    }

    @GetMapping("/hobby/{path}")
    public String viewHobby(@CurrentAccount Account account, @PathVariable String path, Model model) {
       Hobby hobby = hobbyService.getHobby(path);
        model.addAttribute(account);
        model.addAttribute(hobby);
        return "hobby/view";
    }

    @GetMapping("/hobby/{path}/members")
    public String viewStudyMembers(@CurrentAccount Account account, @PathVariable String path, Model model) {
        Hobby hobby = hobbyService.getHobby(path);
        model.addAttribute(account);
        model.addAttribute(hobby);
        return "hobby/members";
    }

}