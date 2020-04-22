package com.hobbytogther.hooby;


import com.hobbytogther.domain.Hobby;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
public interface HobbyRepository extends JpaRepository<Hobby,Long> {
    boolean existsByPath(String path);

//    @EntityGraph(value = "Hobby.withAll", type = EntityGraph.EntityGraphType.LOAD)
//    Hobby findByPath(String path);
//
//    @EntityGraph(value = "Hobby.withTagsAndManagers", type = EntityGraph.EntityGraphType.FETCH)
//    Hobby findHobbyWithTagsByPath(String path);
//
//    @EntityGraph(value = "Hobby.withZonesAndManagers", type = EntityGraph.EntityGraphType.FETCH)
//    Hobby findHobbyWithZonesByPath(String path);
//
//    @EntityGraph(value = "Hobby.withManagers", type = EntityGraph.EntityGraphType.FETCH)
//    Hobby findHobbyWithManagersByPath(String path);
//
//    @EntityGraph(value = "Hobby.withMembers", type = EntityGraph.EntityGraphType.FETCH)
//    Hobby findHobbyWithMembersByPath(String path);


    @EntityGraph(value = "Hobby.withAllRelations", type = EntityGraph.EntityGraphType.LOAD)
    Hobby findByPath(String path);

    @EntityGraph(value = "Hobby.withTagsAndManagers", type = EntityGraph.EntityGraphType.FETCH)
    Hobby findHobbyWithTagsByPath(String path);

    @EntityGraph(value = "Hobby.withZonesAndManagers", type = EntityGraph.EntityGraphType.FETCH)
    Hobby findHobbyWithZonesByPath(String path);

    @EntityGraph(value = "Hobby.withManagers", type = EntityGraph.EntityGraphType.FETCH)
    Hobby findHobbyWithManagersByPath(String path);

    @EntityGraph(value = "Hobby.withMembers", type = EntityGraph.EntityGraphType.FETCH)
    Hobby findHobbyWithMembersByPath(String path);


    boolean existsByTitle(String newTitle);
}
