package com.hobbytogther.modules.account;

import org.springframework.security.core.annotation.AuthenticationPrincipal;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * RunTime 까지 유지가 되어야 한다.
 * param에만 사용
 * 익명 인증인 경우에는 null로 설정하고, 아닌 경우에는 account 프로퍼티를 조회해서 설정
 *                                              UserAccount타입
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
@AuthenticationPrincipal(expression = "#this == 'anonymousUser' ? null : account")
public @interface CurrentAccount {
}
