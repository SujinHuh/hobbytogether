package com.hobbytogther.account;

import com.hobbytogther.domain.Account;
import com.hobbytogther.domain.Tag;
import com.hobbytogther.settings.validator.Notifications;
import com.hobbytogther.settings.form.Profile;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@Transactional
@RequiredArgsConstructor
public class AccountService implements UserDetailsService {

    private final AccountRepository accountRepository;
    private final JavaMailSender javaMailSender;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;


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
                .studyCreatedByWeb(true)
                .studyEnrollmentResultByWeb(true)
                .studyUpdatedByWeb(true)
                .build();


        return accountRepository.save(account);
    }

    public void sendSignUpConfirmEmail(Account newAccount) {
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

    /**
     * 로그인을 처리 할 때 : 데이터 베이스에 저장된 정보를 참조해서 인증 /
     * 데이터 베이스에 있는 정보를 조회 할 수 있는 "UserDetailsService" 만들어 줘야함 <인터페이스는 구현해야함/ 핸드러는 구현 안함>
     */
    @Transactional(readOnly = true)
    @Override
    public UserDetails loadUserByUsername(String emailOrNickname) throws UsernameNotFoundException {
        Account account = accountRepository.findByEmail(emailOrNickname);
        if (account == null) {
            account = accountRepository.findByNickname(emailOrNickname);
        }
        if (account == null) {
            throw new UsernameNotFoundException(emailOrNickname);
        }
        /**
         * 해당하는 User가 있다는 것
         * principal에 해당하는 객체를 넘기면 됨 - springSecurity 확장한 UserAccount extend User
         */
        return new UserAccount(account);
    }

    public void completeSignUp(Account account) {

        account.completeSignUp();
        //회원가입시 자동 로그인
        this.login(account);
    }

    public void updateProfile(Account account, Profile profile) {
        /** modelMapper.map() -> map은 sauce에 있는 data를 목적지로 복사 해주는것*/
        /** 즉, profile에 있는 값을 account에 담아주는 것 , map 입장에서 sauce는 profile , destination은 account */
        modelMapper.map(profile, account);
        accountRepository.save(account);

    }

    public void updatePassword(Account account, String newPassword) {
        account.setPassword(passwordEncoder.encode(newPassword)); //passwordEncoder 변환해줘야 한다.
        accountRepository.save(account); //merge 상태변
    }


    public void updateNotifications(Account account, Notifications notifications) {
        modelMapper.map(notifications, account);
        accountRepository.save(account);
    }

    public void updateNickname(Account account, String nickname) {
        account.setNickname(nickname);
        accountRepository.save(account); //detached 객체라서 db 반영안함 명시적으로 save 해야한다. save하면 merge가 일어난다.
        login(account);// 반영이 안되어서 반영
    }

    public void sendLoginLink(Account account) {
        account.generateEmailCheckToken();
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(account.getEmail());
        mailMessage.setSubject("하비투게더, 로그인 링크");
        mailMessage.setText("/login-by-email?token=" + account.getEmailCheckToken() +
                "&email=" + account.getEmail());
        javaMailSender.send(mailMessage);
    }

    public void addTag(Account account, Tag tag) {
        /** Account detached객체여서 먼저 읽어와야 한다.*/
        /** to many로 끝나는 관계는 값들이 널 값이다.  - Lazy 로딩 불가 detached 객체이니까 */
        Optional<Account> byId = accountRepository.findById(account.getId()); // Eager 패치 읽어옴
        byId.ifPresent(a -> a.getTags().add(tag)); //만약 있으면 account에 tag를 추가하라

//        accountRepository.getOne()/**Lazy 로딩 필요한 순간에만 읽어옴 엔티티 매니저를 통해서 */

    }

    public void removeTag(Account account, Tag tag) {
        Optional<Account> byId = accountRepository.findById(account.getId()); // Eager 패치 읽어옴
        byId.ifPresent(a -> a.getTags().remove(tag));
    }

    public Set<Tag> getTags(Account account) {
        Optional<Account> byId = accountRepository.findById(account.getId());
        return byId.orElseThrow().getTags(); // 없으면 error 있으면 tag정보를
    }
}
