package com.hobbytogther.hobby;

import com.hobbytogther.account.CurrentAccount;
import com.hobbytogther.domain.Account;
import com.hobbytogther.domain.Hobby;
import com.hobbytogther.hobby.form.HobbyDescriptionForm;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Path;
import javax.validation.Valid;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Controller
@RequestMapping("/hobby/{path}/settings")
@RequiredArgsConstructor
public class HobbySettingsController {

    private final HobbyService hobbyService;
    private final ModelMapper modelMapper;

    /** Description 수정 */
    @GetMapping("/description")
    public String viewHobbySetting(@CurrentAccount Account account, @PathVariable String path , Model model) {
        Hobby hobby = hobbyService.getHobbyToUpdate(account,path);
        model.addAttribute(account);
        model.addAttribute(hobby);
        model.addAttribute(modelMapper.map(hobby, HobbyDescriptionForm.class)); //form을 수정
        return "hobby/settings/description";
    }

    /** Description 저장 */
    @PostMapping("/description")
    public String updateHobbyInfo(@CurrentAccount Account account, @PathVariable String path,
                                  @Valid HobbyDescriptionForm hobbyDescriptionForm, Errors errors, Model model,
                                  RedirectAttributes attributes) {

        Hobby hobby = hobbyService.getHobbyToUpdate(account, path); //persist statue
        if(errors.hasErrors()) {
            model.addAttribute(account);
            model.addAttribute(hobby);
            return "hobby/settings/description";
        }
        hobbyService.updateHobbyDescription(hobby,hobbyDescriptionForm);
        attributes.addFlashAttribute("message", "hobby 소개를 수정했습니다.");
        return "redirect:/hobby/" + getPath(path) + "/settings/hobby";
    }
    public String getPath(String path) {
        return URLEncoder.encode(path, StandardCharsets.UTF_8);
    }
}
