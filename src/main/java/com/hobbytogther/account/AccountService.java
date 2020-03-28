package com.hobbytogther.account;

import com.hobbytogther.domain.Account;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.Valid;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final JavaMailSender javaMailSender;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public Account processNewAccount(SignUpForm signUpForm) {

        Account newAccount = saveNewAccount(signUpForm);
        newAccount.generateEmailCheckToken();
        sendSignUpConfirmEmail(newAccount);
        return newAccount;
    }

    private Account saveNewAccount(@Valid SignUpForm signUpForm) {
        Account account = Account.builder()
                .email(signUpForm.getEmail())
                .nickname(signUpForm.getNickname())
                .password(passwordEncoder.encode(signUpForm.getPassword()))
                .studyCreateByEmail(true)
                .studyEnrollmentResultByWeb(true)
                .studyUpdateByWeb(true)
                .build();


        return accountRepository.save(account);
    }

    private void sendSignUpConfirmEmail(Account newAccount) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(newAccount.getEmail());
        mailMessage.setSubject("하비 투게더, 회원 가입 인증");
        mailMessage.setText("/check-email-token?token=" + newAccount.getEmailCheckToken() +
                "&email=" + newAccount.getEmail());

        javaMailSender.send(mailMessage);
    }


    /**
     * 회원 가입시 자동로그인
     * - 인코딩한 패스워드로 접근할 수 밖에 없음
     * 플레인 패스워드가 없어서  signUpSubmit - 접근 가능, checkEmailToken 접근 불가
     */
    public void login(Account account) {
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
                new UserAccount(account),//principal 객체
                account.getPassword(),// password 값
                List.of(new SimpleGrantedAuthority("ROLE_USER")));// 계정이 가지고 있는 권한 / 권한 목록을 보여줌

        /**
         * 정석인 방법
         UsernamePasswordAuthenticationToken token1 = new UsernamePasswordAuthenticationToken(username, password);
         Authentication authentication = authenticationManager.authenticate(tocken);
         */

        SecurityContext context = SecurityContextHolder.getContext();
        context.setAuthentication(token);//token을 설정해 주면 로그인이된다.

    }
}
