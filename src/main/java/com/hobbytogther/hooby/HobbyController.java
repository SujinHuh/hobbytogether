package com.hobbytogther.hooby;

import com.hobbytogther.account.CurrentAccount;
import com.hobbytogther.domain.Account;
import com.hobbytogther.domain.Hobby;
import com.hobbytogther.hooby.form.HobbyForm;
import com.hobbytogther.hooby.validator.HobbyFormValidator;
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
    /** Valid */
    public void hobbyFormInitBinder(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(hobbyFormValidator);
    }

    @GetMapping("/new-hobby")
    public String newStudyForm(@CurrentAccount Account account, Model model) {
        model.addAttribute(account);
        model.addAttribute(new HobbyForm());
        return "hobby/form";
    }

    @PostMapping("/new-hobby")
    public String newHobby(@CurrentAccount Account account, @Valid HobbyForm hobbyForm, Errors errors,Model model) {
        if (errors.hasErrors()) {
            model.addAttribute(account);
            return "hobby/form";
        }
        Hobby newHobby = hobbyService.createNewHobby(modelMapper.map(hobbyForm, Hobby.class), account);
        //Post -> redirect / 해당하는 URL로 보낼 것
        return "redirect:/hobby/" + URLEncoder.encode(newHobby.getPath(), StandardCharsets.UTF_8);
    }

    /** Hobby 조회*/
    @GetMapping("/hobby/{path}")
    public String viewStudy(@CurrentAccount Account account, @PathVariable String path, Model model) {
        Hobby hobby = hobbyService.getHoby(path);
        model.addAttribute(account);
        model.addAttribute(hobby);
        return "hobby/view";
    }

    /** Hobby 구성원 조회 */
    @GetMapping("/hobby/{path}/members")
    public String viewStudyMembers(@CurrentAccount Account account, @PathVariable String path, Model model) {
        Hobby hobby = hobbyService.getHoby(path);
        model.addAttribute(account);
        model.addAttribute(hobby);
        return "hobby/members";
    }


}