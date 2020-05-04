package com.hobbytogther.modules.hobby.validator;

import com.hobbytogther.modules.hobby.Hobby;
import com.hobbytogther.modules.hobby.QHobby;
import com.querydsl.jpa.JPQLQuery;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import java.util.List;

public class HobbyRepositoryExtensionImpl extends QuerydslRepositorySupport implements HobbyRepositoryExtension {

    //상위 클래스에 기본생성자가 없어서 컴파일에러가남 , 상위클래스에 디폴트 생성자가없어서 에러발생함
    public HobbyRepositoryExtensionImpl() {
        super(Hobby.class);
    }

    @Override
    public List<Hobby> findByKeyword(String keyword) {
        QHobby hobby = QHobby.hobby;
        JPQLQuery<Hobby> query = from(hobby).where(hobby.published.isTrue()
                .and(hobby.title.containsIgnoreCase(keyword))
                .or(hobby.tags.any().title.containsIgnoreCase(keyword))
                .or(hobby.zones.any().localNameOfCity.containsIgnoreCase(keyword)));
        return query.fetch();
    }
}