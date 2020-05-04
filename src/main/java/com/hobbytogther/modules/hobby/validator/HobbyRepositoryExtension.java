package com.hobbytogther.modules.hobby.validator;

import com.hobbytogther.modules.hobby.Hobby;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Transactional(readOnly = true)
public interface HobbyRepositoryExtension {

    Page<Hobby> findByKeyword(String keyword, Pageable pageable);

}
