package com.hobbytogther.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Table(name = "persistent_logins")
@Entity
@Getter @Setter
public class PersistentLogins {
    /**
     * InMemoryDB를 사용할때는 Account 엔티티 정보를 보고 테이블을 알아서 만들어준다.
     * JdbcTokenRepositoryImpl 스키마에 해당하는 엔티티를 추가 -> PersistentLogins
     * 테이블이 생성됨
     */
    @Id
    @Column(length = 64)
    private String series;

    @Column(nullable = false, length = 64)
    private String username;

    @Column(nullable = false, length = 64)
    private String token;

    @Column(name = "last_used", nullable = false, length = 64)
    private LocalDateTime lastUsed;

}