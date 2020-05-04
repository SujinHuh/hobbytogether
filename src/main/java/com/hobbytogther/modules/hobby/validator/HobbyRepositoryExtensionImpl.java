package com.hobbytogther.modules.hobby.validator;

import com.hobbytogther.modules.account.QAccount;
import com.hobbytogther.modules.hobby.Hobby;
import com.hobbytogther.modules.hobby.QHobby;
import com.hobbytogther.modules.tag.QTag;
import com.hobbytogther.modules.zone.QZone;
import com.querydsl.core.QueryResults;
import com.querydsl.jpa.JPQLQuery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import java.util.List;

public class HobbyRepositoryExtensionImpl extends QuerydslRepositorySupport implements HobbyRepositoryExtension {

    //상위 클래스에 기본생성자가 없어서 컴파일에러가남 , 상위클래스에 디폴트 생성자가없어서 에러발생함
    public HobbyRepositoryExtensionImpl() {
        super(Hobby.class);
    }

    @Override
    public Page<Hobby> findByKeyword(String keyword, Pageable pageable) {
        QHobby hobby = QHobby.hobby;
        JPQLQuery<Hobby> query = from(hobby).where(hobby.published.isTrue()
                .and(hobby.title.containsIgnoreCase(keyword))
                .or(hobby.tags.any().title.containsIgnoreCase(keyword))
                .or(hobby.zones.any().localNameOfCity.containsIgnoreCase(keyword)))
                /** N+1 문제 해결 */
                .leftJoin(hobby.tags, QTag.tag).fetchJoin()//fetchJoin을하기 위해서는 leftJoin이 있어야 한다. .fetchJoin()은 Join한 Data를 가져오는 것
                .leftJoin(hobby.zones, QZone.zone).fetchJoin()
                .leftJoin(hobby.members, QAccount.account).fetchJoin()
                .distinct()//중복 데이터 해결
                //결과중에서 유일한 값만 결과 값이 나옴
                //쿼리 결과 최적화 : 1. distinct를 빼는 것(의미 없기때문에) resultTransformer를 제공
                ;
        /**leftJoin만 했을때 결과 3개가 나오는지
            letfJoin 왼쪽에 해당하는 데이터를 다가져오는 것 -> 오른쪽에 mapping되는 데이터를 '같이'가져온다.
         */

        JPQLQuery<Hobby> pageableQuery = getQuerydsl().applyPagination(pageable, query);
        QueryResults<Hobby> hobbyFetchResults = pageableQuery.fetchResults();//fetchResults를 사용 페이징과 관련된 데이터까지 가져옴  / fech는 data만 가져옴


        return new PageImpl<>(hobbyFetchResults.getResults(), pageable,hobbyFetchResults.getTotal());
    }
}