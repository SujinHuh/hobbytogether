package com.hobbytogther.event.validator;

import com.hobbytogther.domain.Event;
import com.hobbytogther.event.EventForm;
import com.hobbytogther.event.EventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class EventValidator implements Validator {


    @Override
    public boolean supports(Class<?> clazz) {
        return EventForm.class.isAssignableFrom(clazz);
    }
    @Override
    public void validate(Object target, Errors errors) {
        EventForm eventForm = (EventForm) target;

        if (isNotValidEndEnrollmentTime(eventForm)) {
            errors.rejectValue("endEnrollmentDateTime", "wrong.datetime", "모임 '접수' 일시를 정확하게 입력 해주세요");
        }
        /** 모임 종료하는 일시 */
        if (isNotValidEndDateTime(eventForm)) {
            errors.rejectValue("endDateTime", "wrong.datetime", "모임 '종료' 일시를 정확하게 입력 해주세요");
        }
        if(isNotValidStartDateTime(eventForm)) {
            errors.rejectValue("startDateTime", "wrong.datetime", "모임 '시작' 일시를 정확하게 입력 해주세요");
        }
    }

    private boolean isNotValidStartDateTime(EventForm eventForm) {
        return eventForm.getStartDateTime().isBefore(eventForm.getEndEnrollmentDateTime());
    }

    private boolean isNotValidEndEnrollmentTime(EventForm eventForm) {
        return eventForm.getEndEnrollmentDateTime().isBefore(LocalDateTime.now());
    }

    private boolean isNotValidEndDateTime(EventForm eventForm) {
        LocalDateTime endDateTime = eventForm.getEndDateTime();
        return endDateTime.isBefore(eventForm.getStartDateTime()) || endDateTime.isBefore(eventForm.getEndEnrollmentDateTime());
    }

    public void validateUpdateForm(EventForm eventForm, Event event, Errors errors) {
        if (eventForm.getLimitOfEnrollments() < event.getNumberOfAcceptedEnrollments()) {
            errors.rejectValue("limitOfEnrollments", "wrong.value", "확인된 참기 신청보다 모집 인원 수가 커야 합니다.");
        }
    }
}
