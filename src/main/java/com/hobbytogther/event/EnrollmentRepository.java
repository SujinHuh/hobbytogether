package com.hobbytogther.event;

import com.hobbytogther.domain.Account;
import com.hobbytogther.domain.Enrollment;
import com.hobbytogther.domain.Event;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {
    boolean existsByEventAndAccount(Event event, Account account);

    Enrollment findByEventAndAccount(Event event, Account account);
}