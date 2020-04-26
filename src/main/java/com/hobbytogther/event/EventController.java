package com.hobbytogther.event;

import com.hobbytogther.account.CurrentAccount;
import com.hobbytogther.domain.Account;
import com.hobbytogther.domain.Hobby;
import com.hobbytogther.hobby.validator.HobbyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/hobby/{path}")
@RequiredArgsConstructor
public class EventController {

    private final HobbyService hobbyService;


    @GetMapping("/new-event")
    public String newEventForm(@CurrentAccount Account account, @PathVariable String path, Model model) {
        Hobby hobby = hobbyService.getHobbyToUpdateStatus(account, path);//Manager만 가져온것

        model.addAttribute(hobby);
        model.addAttribute(account);
        model.addAttribute(new EventForm());
        return "event/form";
    }
}
