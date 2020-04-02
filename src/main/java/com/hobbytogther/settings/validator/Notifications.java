package com.hobbytogther.settings.validator;

import com.hobbytogther.domain.Account;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class Notifications {

    private boolean studyCreatedByEmail;

    private boolean studyCreatedByWeb;

    private boolean studyEnrollmentResultByEmail;

    private boolean studyEnrollmentResultByWeb;

    private boolean studyUpdatedByEmail;

    private boolean studyUpdatedByWeb;

    /** @Bean을 주입 받을 수 없다. Bean이 아니니까 Notifications은 bean이아니다.
            new 생성자를 호출해서 만들어야 */

}