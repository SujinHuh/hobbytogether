package com.hobbytogther.event;

import com.hobbytogther.WithAccount;
import com.hobbytogther.account.AccountFactory;
import com.hobbytogther.account.AccountRepository;
import com.hobbytogther.account.AccountService;
import com.hobbytogther.account.SignUpForm;
import com.hobbytogther.domain.Account;
import com.hobbytogther.domain.Event;
import com.hobbytogther.domain.EventType;
import com.hobbytogther.domain.Hobby;
import com.hobbytogther.hobby.validator.HobbyRepository;
import com.hobbytogther.hobby.validator.HobbyService;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;


import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
@SpringBootTest
@AutoConfigureMockMvc
@RequiredArgsConstructor
class EventControllerTest {

    @Autowired
    AccountFactory accountFactory;
    @Autowired
    AccountRepository accountRepository;
    @Autowired
    HobbyService hobbyService;
    @Autowired
    EventService eventService;
    @Autowired
    MockMvc mockMvc;
    @Autowired
    EnrollmentRepository enrollmentRepository;

    public Hobby createHobby(String path, Account manager) {
        Hobby hobby = new Hobby();
        hobby.setPath(path);
        hobbyService.createNewHobby(hobby, manager);
        return hobby;
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

    private void isAccepted(Account account, Event event) {
        assertTrue(enrollmentRepository.findByEventAndAccount(event, account).isAccepted());
    }

    @Test
    @DisplayName("선착순 모임 참가 신청 - 자동수락")
    @WithAccount("sujin")
    public void newEnrollment_FCFS_accept() throws Exception {

        //given
        Account sunshine = accountFactory.createAccount("sunshine");
        Hobby hobby = createHobby("test-hobby", sunshine);
        Event event = createEvent("test-event", EventType.FCFS, 2, hobby, sunshine);
        //when

        mockMvc.perform(post("/hobby/" + hobby.getPath() + "/events/" + event.getId() + "/enroll")
                .with(csrf()))
                .andExpect(status().is3xxRedirection()) // 성공적으로 redirect가 될 것이다.
                .andExpect(redirectedUrl("/hobby/" + hobby.getPath() + "/events/" + event.getId()));

        Account sujin = accountRepository.findByNickname("sujin");
        isAccepted(sujin, event);
        //then

    }

}