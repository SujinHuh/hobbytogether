package com.hobbytogther.domain;

import com.hobbytogther.account.UserAccount;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@NamedEntityGraph(
        name = "Event.withEnrollments",
        attributeNodes = @NamedAttributeNode("enrollments")
)

@Entity
@Getter @Setter @EqualsAndHashCode(of ="id")
public class Event {

    @Id @GeneratedValue
    private Long id;

    @ManyToOne
    private Hobby hobby;

    @ManyToOne
    private Account createdBy;

    @Column(nullable = false)
    private String title;

    @Lob
    private String description;

    @Column(nullable = false)
    private LocalDateTime createdDateTime;

    @Column(nullable = false)
    private LocalDateTime endEnrollmentDateTime;

    @Column(nullable = false)
    private LocalDateTime startDateTime;

    @Column(nullable = false)
    private LocalDateTime endDateTime;

    @Column(nullable = true)
    private Integer limitOfEnrollments;

    @OneToMany(mappedBy = "event")  //관걔 맵핑
    private List<Enrollment> enrollments;

    @Enumerated(EnumType.STRING)
    private EventType eventType;

    public boolean isEnrollableFor(UserAccount userAccount) {
        return isNotClosed() && !isAlreadyEnrolled(userAccount); //이벤트 종료 되지 않았고, 참석하지 않았으면
    }

    /** 참석 취소 */
    public boolean isDisenrollableFor(UserAccount userAccount) {
        return isNotClosed() && isAlreadyEnrolled(userAccount);
    }

    private boolean isNotClosed() {
        return this.endEnrollmentDateTime.isAfter(LocalDateTime.now());
    }

    /** 참석 했는지 여부 */
    public boolean isAttended(UserAccount userAccount) {
        Account account = userAccount.getAccount();
        for (Enrollment e : this.enrollments) {
            if (e.getAccount().equals(account) && e.isAttended()) {
                return true;
            }
        }

        return false;
    }

    private boolean isAlreadyEnrolled(UserAccount userAccount) {
        Account account = userAccount.getAccount();
        for (Enrollment e : this.enrollments) {
            if (e.getAccount().equals(account)) {
                return true;
            }
        }
        return false;
    }
    /** 자리 남음 */
    public int numberOfRemainSpots() {
        return this.limitOfEnrollments - (int) this.enrollments.stream().filter(Enrollment::isAccepted).count();
    }


    public long getNumberOfAcceptedEnrollments() {
        return this.enrollments.stream().filter(Enrollment::isAccepted).count();
    }
}
