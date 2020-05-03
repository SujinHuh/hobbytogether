package com.hobbytogther.modules.event;

import com.hobbytogther.modules.account.Account;
import com.hobbytogther.modules.event.event.EnrollmentAcceptedEvent;
import com.hobbytogther.modules.event.event.EnrollmentRejectedEvent;
import com.hobbytogther.modules.hobby.Hobby;
import com.hobbytogther.modules.event.form.EventForm;
import com.hobbytogther.modules.hobby.event.HobbyUpdateEvent;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Transactional
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;
    private final ModelMapper modelMapper;
    private final EnrollmentRepository enrollmentRepository;
    private final ApplicationEventPublisher applicationEventPublisher;

    public Event createEvent(Event event, Hobby hobby, Account account) {
        event.setCreatedBy(account);
        event.setCreatedDateTime(LocalDateTime.now());
        event.setHobby(hobby);
        applicationEventPublisher.publishEvent(new HobbyUpdateEvent(event.getHobby(),
                "'" + event.getTitle() + "' 모임을 만들었습니다."));
        return eventRepository.save(event);
    }

    public void updateEvent(Event event, EventForm eventForm) {
        modelMapper.map(eventForm, event);
        event.acceptWaitingList();
        applicationEventPublisher.publishEvent(new HobbyUpdateEvent(event.getHobby(),
                "'" + event.getTitle() + "' 모임 정보를 수정했으니 확인하세요."));
    }
    public void deleteEvent(Event event) {
        eventRepository.delete(event);
        applicationEventPublisher.publishEvent(new HobbyUpdateEvent(event.getHobby(),
                "'" + event.getTitle() + "' 모임을 취소했습니다."));
    }

    public void newEnrollment(Event event, Account account) {
        if (!enrollmentRepository.existsByEventAndAccount(event, account)) {
            Enrollment enrollment = new Enrollment();
            enrollment.setEnrolledAt(LocalDateTime.now());
            enrollment.setAccepted(event.isAbleToAcceptWaitingEnrollment()); //상황에 알잦게 신청과 동시에 확정상태
            enrollment.setAccount(account);
            event.addEnrollment(enrollment);
            enrollmentRepository.save(enrollment);
        }
    }

    public void cancelEnrollment(Event event, Account account) {
        Enrollment enrollment = enrollmentRepository.findByEventAndAccount(event, account);
        if (!enrollment.isAttended()) {
            event.removeEnrollment(enrollment);
            enrollmentRepository.delete(enrollment);
            event.acceptNextWaitingEnrollment();
        }
    }

    public void acceptEnrollment(Event event, Enrollment enrollment) {
        event.accept(enrollment);
        applicationEventPublisher.publishEvent(new EnrollmentAcceptedEvent(enrollment));

    }

    public void rejectEnrollment(Event event, Enrollment enrollment) {
        event.reject(enrollment);
        applicationEventPublisher.publishEvent(new EnrollmentRejectedEvent(enrollment));

    }

    public void checkInEnrollment(Enrollment enrollment) {
        enrollment.setAttended(true);
    }

    public void cancelCheckInEnrollment(Enrollment enrollment) {
        enrollment.setAttended(false);
    }


}
