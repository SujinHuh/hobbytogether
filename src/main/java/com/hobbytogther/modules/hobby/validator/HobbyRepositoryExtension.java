package com.hobbytogther.modules.hobby.validator;

import com.hobbytogther.modules.hobby.Hobby;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Transactional(readOnly = true)
public interface HobbyRepositoryExtension {

    List<Hobby> findByKeyword(String keyword);

}
