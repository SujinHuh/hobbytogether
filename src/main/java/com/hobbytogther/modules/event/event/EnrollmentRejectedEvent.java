package com.hobbytogther.modules.event.event;


import com.hobbytogther.modules.account.Account;
import com.hobbytogther.modules.event.Enrollment;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Transactional(readOnly = true)
public class EnrollmentRejectedEvent extends EnrollmentEvent {

    public EnrollmentRejectedEvent(Enrollment enrollment) {
        super(enrollment, "모임 참가 신청을 거절했습니다.");
    }

//    @EntityGraph("Enrollment.withEventAndHobby")
//    List<Enrollment> findByAccountAndAcceptedOrderByEnrolledAtDesc(Account account, boolean accepted);

}