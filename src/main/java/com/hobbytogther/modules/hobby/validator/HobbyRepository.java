package com.hobbytogther.modules.hobby.validator;

import com.hobbytogther.modules.account.Account;
import com.hobbytogther.modules.hobby.Hobby;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly = true)
public interface HobbyRepository extends JpaRepository<Hobby ,Long> , HobbyRepositoryExtension {
    boolean existsByPath(String path);

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

    Hobby findHobbyOnlyByPath(String path);

    @EntityGraph(value = "Hobby.withTagsAndZones", type = EntityGraph.EntityGraphType.FETCH)
    Hobby findHobbyWithTagsAndZonesById(Long id);

    @EntityGraph(attributePaths = {"members", "managers"})
    Hobby findHobbyWithManagersAndMembersById(Long id);

    @EntityGraph(attributePaths = {"zones", "tags"})
    List<Hobby> findFirst9ByPublishedAndClosedOrderByPublishedDateTimeDesc(boolean published, boolean closed);

    List<Hobby> findFirst5ByManagersContainingAndClosedOrderByPublishedDateTimeDesc(Account account, boolean closed);

    List<Hobby> findFirst5ByMembersContainingAndClosedOrderByPublishedDateTimeDesc(Account account, boolean closed);
}

