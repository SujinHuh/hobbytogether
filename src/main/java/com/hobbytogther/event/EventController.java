package com.hobbytogther.event;

import com.hobbytogther.account.CurrentAccount;
import com.hobbytogther.domain.Account;
import com.hobbytogther.domain.Event;
import com.hobbytogther.domain.Hobby;
import com.hobbytogther.event.validator.EventValidator;
import com.hobbytogther.hobby.validator.HobbyService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Controller
@RequestMapping("/hobby/{path}")
@RequiredArgsConstructor
public class EventController {

    private final HobbyService hobbyService;
    private final EventService eventService;
    private final ModelMapper modelMapper;
    private final EventValidator eventValidator;
    private final EventRepository eventRepository;

    @InitBinder("eventForm")
    public void initBinder(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(eventValidator);
    }

    @GetMapping("/new-event")
    public String newEventForm(@CurrentAccount Account account, @PathVariable String path, Model model) {
        Hobby hobby = hobbyService.getHobbyToUpdateStatus(account, path);//Manager만 가져온것

        model.addAttribute(hobby);
        model.addAttribute(account);
        model.addAttribute(new EventForm());
        return "event/form";
    }

    @PostMapping("/new-event")
    public String newEventSubmit(@CurrentAccount Account account , @PathVariable String path,
                                 @Valid EventForm eventForm, Errors errors, Model model) {

        Hobby hobby = hobbyService.getHobbyToUpdateStatus(account, path);
        if(errors.hasErrors()) {
            model.addAttribute(account);
            model.addAttribute(hobby);
            return "event/form";
        }
        Event event = eventService.createEvent(modelMapper.map(eventForm, Event.class), hobby, account);//event를 eventform으로 변경해야 함
        return "redirect:/hobby/" + hobby.getEncodedPath() + "/events/" + event.getId();
    }

    /** Events 조회 */
    @GetMapping("/events/{id}")
    public String getEvent(@CurrentAccount Account account, @PathVariable String path, @PathVariable Long id, Model model) {

        model.addAttribute(account);
        model.addAttribute(eventRepository.findById(id).orElseThrow());
        model.addAttribute(hobbyService.getHobby(path));

        return "event/view";
    }
}
