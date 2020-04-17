package com.hobbytogther.hooby;


import com.hobbytogther.domain.Hobby;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
public interface HobbyRepository extends JpaRepository<Hobby,Long> {
    boolean existsByPath(String path);

    @EntityGraph(value = "Hobby.withAll", type = EntityGraph.EntityGraphType.LOAD)
    Hobby findByPath(String path);

}