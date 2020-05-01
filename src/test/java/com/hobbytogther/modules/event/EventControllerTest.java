package com.hobbytogther.modules.event;
import com.hobbytogther.WithAccount;
import com.hobbytogther.modules.account.Account;
import com.hobbytogther.modules.account.AccountFactory;
import com.hobbytogther.modules.account.AccountRepository;
import com.hobbytogther.modules.hobby.Hobby;
import com.hobbytogther.modules.hobby.HobbyControllerTest;
import com.hobbytogther.modules.hobby.HobbyFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class EventControllerTest extends HobbyControllerTest {


    @Autowired
    MockMvc mockMvc;
    @Autowired
    HobbyFactory hobbyFactory;
    @Autowired
    AccountFactory accountFactory;
    @Autowired
    EventService eventService;
    @Autowired
    EnrollmentRepository enrollmentRepository;
    @Autowired
    AccountRepository accountRepository;

    @Test
    @DisplayName("선착순 모임에 참가 신청 - 자동 수락")
    @WithAccount("sujin")
    void newEnrollment_to_FCFS_event_accepted() throws Exception {
        Account huhSu = accountFactory.createAccount("huhSu");
        Hobby hobby = hobbyFactory.createHobby("test-hobby", huhSu);
        Event event = createEvent("test-event", EventType.FCFS, 2, hobby, huhSu);

        mockMvc.perform(post("/hobby/" + hobby.getPath() + "/events/" + event.getId() + "/enroll")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/hobby/" + hobby.getPath() + "/events/" + event.getId()));

        Account sujin = accountRepository.findByNickname("sujin");
        isAccepted(sujin, event);
    }

    @Test
    @DisplayName("선착순 모임에 참가 신청 - 대기중 (이미 인원이 꽉차서)")
    @WithAccount("sujin")
    void newEnrollment_to_FCFS_event_not_accepted() throws Exception {
        Account huhSu = accountFactory.createAccount("huhSu");
        Hobby hobby = hobbyFactory.createHobby("test-hobby", huhSu);
        Event event = createEvent("test-event", EventType.FCFS, 2, hobby, huhSu);

        Account may = accountFactory.createAccount("may");
        Account june = accountFactory.createAccount("june");
        eventService.newEnrollment(event, may);
        eventService.newEnrollment(event, june);

        mockMvc.perform(post("/hobby/" + hobby.getPath() + "/events/" + event.getId() + "/enroll")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/hobby/" + hobby.getPath() + "/events/" + event.getId()));

        Account sujin = accountRepository.findByNickname("sujin");
        isNotAccepted(sujin, event);
    }

    @Test
    @DisplayName("참가신청 확정자가 선착순 모임에 참가 신청을 취소하는 경우, 바로 다음 대기자를 자동으로 신청 확인한다.")
    @WithAccount("sujin")
    void accepted_account_cancelEnrollment_to_FCFS_event_not_accepted() throws Exception {
        Account sujin = accountRepository.findByNickname("sujin");
        Account huhSu = accountFactory.createAccount("huhSu");
        Account may = accountFactory.createAccount("may");
        Hobby hobby = hobbyFactory.createHobby("test-hobby", huhSu);
        Event event = createEvent("test-event", EventType.FCFS, 2, hobby, huhSu);

        eventService.newEnrollment(event, may);
        eventService.newEnrollment(event, sujin);
        eventService.newEnrollment(event, huhSu);

        isAccepted(may, event);
        isAccepted(sujin, event);
        isNotAccepted(huhSu, event);

        mockMvc.perform(post("/hobby/" + hobby.getPath() + "/events/" + event.getId() + "/disenroll")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/hobby/" + hobby.getPath() + "/events/" + event.getId()));

        isAccepted(may, event);
        isAccepted(huhSu, event);
        assertNull(enrollmentRepository.findByEventAndAccount(event, sujin));
    }

    @Test
    @DisplayName("참가신청 비확정자가 선착순 모임에 참가 신청을 취소하는 경우, 기존 확정자를 그대로 유지하고 새로운 확정자는 없다.")
    @WithAccount("sujin")
    void not_accepterd_account_cancelEnrollment_to_FCFS_event_not_accepted() throws Exception {
        Account sujin = accountRepository.findByNickname("sujin");
        Account huhSu = accountFactory.createAccount("huhSu");
        Account may = accountFactory.createAccount("may");
        Hobby hobby = hobbyFactory.createHobby("test-hobby", huhSu);
        Event event = createEvent("test-event", EventType.FCFS, 2, hobby, huhSu);

        eventService.newEnrollment(event, may);
        eventService.newEnrollment(event, huhSu);
        eventService.newEnrollment(event, sujin);

        isAccepted(may, event);
        isAccepted(huhSu, event);
        isNotAccepted(sujin, event);

        mockMvc.perform(post("/hobby/" + hobby.getPath() + "/events/" + event.getId() + "/disenroll")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/hobby/" + hobby.getPath() + "/events/" + event.getId()));

        isAccepted(may, event);
        isAccepted(huhSu, event);
        assertNull(enrollmentRepository.findByEventAndAccount(event, sujin));
    }

    private void isNotAccepted(Account huhSu, Event event) {
        assertFalse(enrollmentRepository.findByEventAndAccount(event, huhSu).isAccepted());
    }

    private void isAccepted(Account account, Event event) {
        assertTrue(enrollmentRepository.findByEventAndAccount(event, account).isAccepted());
    }

    @Test
    @DisplayName("관리자 확인 모임에 참가 신청 - 대기중")
    @WithAccount("sujin")
    void newEnrollment_to_CONFIMATIVE_event_not_accepted() throws Exception {
        Account huhSu = accountFactory.createAccount("huhSu");
        Hobby hobby = hobbyFactory.createHobby("test-hobby", huhSu);
        Event event = createEvent("test-event", EventType.CONFIRMATIVE, 2, hobby, huhSu);

        mockMvc.perform(post("/hobby/" + hobby.getPath() + "/events/" + event.getId() + "/enroll")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/hobby/" + hobby.getPath() + "/events/" + event.getId()));

        Account sujin = accountRepository.findByNickname("sujin");
        isNotAccepted(sujin, event);
    }

    private Event createEvent(String eventTitle, EventType eventType, int limit, Hobby hobby, Account account) {
        Event event = new Event();
        event.setEventType(eventType);
        event.setLimitOfEnrollments(limit);
        event.setTitle(eventTitle);
        event.setCreatedDateTime(LocalDateTime.now());
        event.setEndEnrollmentDateTime(LocalDateTime.now().plusDays(1));
        event.setStartDateTime(LocalDateTime.now().plusDays(1).plusHours(5));
        event.setEndDateTime(LocalDateTime.now().plusDays(1).plusHours(7));
        return eventService.createEvent(event, hobby, account);
    }
} 