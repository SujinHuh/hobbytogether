package com.hobbytogther.event;

import com.hobbytogther.account.CurrentAccount;
import com.hobbytogther.domain.Account;
import com.hobbytogther.domain.Event;
import com.hobbytogther.domain.Hobby;
import com.hobbytogther.event.validator.EventValidator;
import com.hobbytogther.hobby.validator.HobbyRepository;
import com.hobbytogther.hobby.validator.HobbyService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/hobby/{path}")
@RequiredArgsConstructor
public class EventController {

    private final HobbyService hobbyService;
    private final HobbyRepository hobbyRepository;
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
        model.addAttribute(hobbyRepository.findHobbyWithManagersByPath(path));

        return "event/view";
    }

    /** Event 목록 조회 */
    @GetMapping("/events")
    public String viewHobbyEvents(@CurrentAccount Account account, @PathVariable String path, Model model) {
        Hobby hobby = hobbyService.getHobby(path);
        model.addAttribute(account);
        model.addAttribute(hobby);

        List<Event> events = eventRepository.findByHobbyOrderByStartDateTime(hobby);
        List<Event> newEvents = new ArrayList<>();
        List<Event> oldEvents = new ArrayList<>();
        events.forEach(e -> {
            if (e.getEndDateTime().isBefore(LocalDateTime.now())) {
                oldEvents.add(e);
            } else {
                newEvents.add(e);
            }
        });

        model.addAttribute("newEvents", newEvents);
        model.addAttribute("oldEvents", oldEvents);

        return "hobby/events";
    }

    /** Event 수정 */
    @GetMapping("/events/{id}/edit")
    public String updateEventForm(@CurrentAccount Account account,
                                  @PathVariable String path, @PathVariable Long id, Model model) {
        Hobby hobby = hobbyService.getHobbyToUpdate(account, path);
        Event event = eventRepository.findById(id).orElseThrow();
        model.addAttribute(hobby);
        model.addAttribute(account);
        model.addAttribute(event);
        model.addAttribute(modelMapper.map(event, EventForm.class)); //view 전달
        return "event/update-form";
    }

  
    @PostMapping("/events/{id}/edit")
    public String updateEventSubmit(@CurrentAccount Account account, @PathVariable String path,
                                    @PathVariable Long id, @Valid EventForm eventForm, Errors errors,
                                    Model model) {
        Hobby hobby = hobbyService.getHobbyToUpdate(account, path);
        Event event = eventRepository.findById(id).orElseThrow();
        eventForm.setEventType(event.getEventType());
        eventValidator.validateUpdateForm(eventForm, event, errors);

        if (errors.hasErrors()) {
            model.addAttribute(account);
            model.addAttribute(hobby);
            model.addAttribute(event);
            return "event/update-form";
        }

        eventService.updateEvent(event, eventForm);
        return "redirect:/hobby/" + hobby.getEncodedPath() +  "/events/" + event.getId();
    }
}
