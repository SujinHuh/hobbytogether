package com.hobbytogther.hobby;


import com.hobbytogther.domain.Hobby;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
public interface HobbyRepository extends JpaRepository<Hobby,Long> {
    boolean existsByPath(String path);
}
