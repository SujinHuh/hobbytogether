package com.hobbytogther.modules.event;

import com.hobbytogther.modules.account.Account;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@NamedEntityGraph(
        name = "Enrollment.withEventAndHobby",
        attributeNodes = {
                @NamedAttributeNode(value = "event", subgraph = "hobby")
        },
        /** 직접적으로 연관관계에 있는 Enrollment 가지고 있는 Hobby 까지 같이가져오는 것 * 서브쿼리 등록 >*/
        subgraphs = @NamedSubgraph(name = "hobby", attributeNodes = @NamedAttributeNode("hobby"))
)

@Entity
@Getter @Setter @EqualsAndHashCode(of = "id")
public class Enrollment {

    @Id @GeneratedValue
    private Long id;

    @ManyToOne
    private Event event;

    @ManyToOne
    private Account account;

    private LocalDateTime enrolledAt;

    private boolean accepted;

    private boolean attended;

}
