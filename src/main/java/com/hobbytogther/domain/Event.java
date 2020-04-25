package com.hobbytogther.domain;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Columns;

import javax.lang.model.element.AnnotationValueVisitor;
import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

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
}
