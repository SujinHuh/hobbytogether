package com.hobbytogther.modules.event;

import com.hobbytogther.modules.account.CurrentAccount;
import com.hobbytogther.modules.account.Account;
import com.hobbytogther.modules.hobby.Hobby;
import com.hobbytogther.modules.event.form.EventForm;
import com.hobbytogther.modules.event.validator.EventValidator;
import com.hobbytogther.modules.hobby.validator.HobbyRepository;
import com.hobbytogther.modules.hobby.validator.HobbyService;
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
    private final EventService eventService;
    private final ModelMapper modelMapper;
    private final EventValidator eventValidator;
    private final EventRepository eventRepository;
    private final HobbyRepository hobbyRepository;
    private final EnrollmentRepository enrollmentRepository;


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
    public String newEventSubmit(@CurrentAccount Account account, @PathVariable String path,
                                 @Valid EventForm eventForm, Errors errors, Model model) {
        Hobby hobby = hobbyService.getHobbyToUpdateStatus(account, path);
        if(errors.hasErrors()) {
            model.addAttribute(account);
            model.addAttribute(hobby);
            return "event/form";
        }
        Event event = eventService.createEvent(modelMapper.map(eventForm, Event.class), hobby, account);
        return "redirect:/hobby/" + hobby.getEncodedPath() + "/events/" +event.getId();
    }

    @GetMapping("/events/{id}") /** Entity converter*/
    public String getEvent(@CurrentAccount Account account, @PathVariable String path,@PathVariable("id") Event event, Model model) {
        model.addAttribute(account);
        model.addAttribute(event);
        model.addAttribute(hobbyRepository.findHobbyWithManagersByPath(path));

        return "event/view";
    }

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

    @GetMapping("/events/{id}/edit")
    public String updateEventForm(@CurrentAccount Account account,
                                  @PathVariable String path,@PathVariable("id") Event event, @PathVariable Long id, Model model) {
        Hobby hobby = hobbyService.getHobbyToUpdate(account, path);
        model.addAttribute(hobby);
        model.addAttribute(account);
        model.addAttribute(event);
        model.addAttribute(modelMapper.map(event, EventForm.class));
        return "event/update-form";
    }
    /** Event Edit */
    @PostMapping("/events/{id}/edit")
    public String updateEventSubmit(@CurrentAccount Account account, @PathVariable String path, @PathVariable("id") Event event,
                                    @PathVariable Long id, @Valid EventForm eventForm, Errors errors,
                                    Model model) {
        Hobby hobby = hobbyService.getHobbyToUpdate(account, path);

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

    /** Event Delete */
    @DeleteMapping("/events/{id}")
    public String cancelEvent(@CurrentAccount Account account, @PathVariable String path,
                              @PathVariable Long id) {
        Hobby hobby = hobbyService.getHobbyToUpdateStatus(account, path);
        eventService.deleteEvent(eventRepository.findById(id).orElseThrow());
        return "redirect:/hobby/" + hobby.getEncodedPath() + "/events";
    }

    @PostMapping("/events/{id}/enroll")
    public String newEnrollment(@CurrentAccount Account account,
                                @PathVariable String path, @PathVariable Long id) {
        Hobby hobby = hobbyService.getHobbyToEnroll(path); //관리자 권한이 아니여도됨
        eventService.newEnrollment(eventRepository.findById(id).orElseThrow(), account);
        return "redirect:/hobby/" + hobby.getEncodedPath() +  "/events/" + id;
    }

    @PostMapping("/events/{id}/disenroll")
    public String cancelEnrollment(@CurrentAccount Account account,
                                   @PathVariable String path,@PathVariable("id") Event event) {
        Hobby hobby = hobbyService.getHobbyToEnroll(path);
        eventService.cancelEnrollment(event, account);
        return "redirect:/hobby/" + hobby.getEncodedPath() + "/events/" + event.getId();
    }

    @GetMapping("events/{eventId}/enrollments/{enrollmentId}/accept")
    public String acceptEnrollment(@CurrentAccount Account account, @PathVariable String path,
                                   @PathVariable("eventId") Event event, @PathVariable("enrollmentId") Enrollment enrollment) {
        Hobby hobby = hobbyService.getHobbyToUpdate(account, path);
        eventService.acceptEnrollment(event, enrollment);
        return "redirect:/hobby/" + hobby.getEncodedPath() + "/events/" + event.getId();
    }

    @GetMapping("/events/{eventId}/enrollments/{enrollmentId}/reject")
    public String rejectEnrollment(@CurrentAccount Account account, @PathVariable String path,
                                   @PathVariable("eventId") Event event, @PathVariable("enrollmentId") Enrollment enrollment) {
        Hobby hobby = hobbyService.getHobbyToUpdate(account, path);
        eventService.rejectEnrollment(event, enrollment);
        return "redirect:/hobby/" + hobby.getEncodedPath() + "/events/" + event.getId();
    }

    @GetMapping("/events/{eventId}/enrollments/{enrollmentId}/checkin")
    public String checkInEnrollment(@CurrentAccount Account account, @PathVariable String path,
                                    @PathVariable("eventId") Event event, @PathVariable("enrollmentId") Enrollment enrollment) {
        Hobby hobby = hobbyService.getHobbyToUpdate(account, path);
        eventService.checkInEnrollment(enrollment);
        return "redirect:/hobby/" + hobby.getEncodedPath() + "/events/" + event.getId();
    }

    @GetMapping("/events/{eventId}/enrollments/{enrollmentId}/cancel-checkin")
    public String cancelCheckInEnrollment(@CurrentAccount Account account, @PathVariable String path,
                                          @PathVariable("eventId") Event event, @PathVariable("enrollmentId") Enrollment enrollment) {
        Hobby hobby = hobbyService.getHobbyToUpdate(account, path);
        eventService.cancelCheckInEnrollment(enrollment);
        return "redirect:/hobby/" + hobby.getEncodedPath() + "/events/" + event.getId();
    }

}
