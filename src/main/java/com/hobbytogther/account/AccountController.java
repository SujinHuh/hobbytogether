package com.hobbytogther.account;

import com.hobbytogther.domain.Account;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;
import java.time.LocalDateTime;

@Controller
@RequiredArgsConstructor
public class AccountController {

    private final SignUpFormValidator signUpFormValidator;
    private final AccountService accountService;
    private final AccountRepository accountRepository;

    @InitBinder("signUpForm")
    public void initBinder(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(signUpFormValidator);
    }

    @GetMapping("/sign-up")
    public String signUpFrom(Model model) {
        model.addAttribute("signUpForm", new SignUpForm());
        return "account/sign-up";
    }

    @PostMapping("/sign-up")
    public String signUpSubmit(@Valid SignUpForm signUpForm, Errors errors) { //플레인 패스워드 접근 가능
        if (errors.hasErrors()) {
            return "account/sign-up";
        }
        Account account = accountService.processNewAccount(signUpForm);

         // 회원 가입시 "자동 로그인"
        accountService.login(account);
        return "redirect:/";
    }

    /**
     * 회원 가입 인증 메일 확인
     * 1. 이메일이 정확하지 않은 경우 에러 처리
     * 2. 토큰이 정확하지 않은 경우에 대한 에러 처리
     * 3. 이메일, 토큰 정확한 경우 가입 완료 처리 - 이메일 인증 여부 true, 가입 일시 기록
     */
    @GetMapping("/check-email-token")
    public String checkEmailToken(String token, String email, Model model) {
        Account account = accountRepository.findByEmail(email);
        String view = "account/checked-email";
        if (account == null) {
            model.addAttribute("error", "wrong email");
            return view;
        }

        if (!account.isValidToken(token)) {
            model.addAttribute("error", "wrong token");
            return view;
        }

        account.completeSignUp();

        //회원가입시 자동 로그인
        accountService.login(account);
        model.addAttribute("numberOfUser", accountRepository.count());
        model.addAttribute("nickname", account.getNickname());
        return view;
    }

    /**
     * checkEmail
     가입 확인 이메일을 전송한 이메일 주소 화면 아래 보여붐
     재전송 버튼 보여주기
     재전송 버튼 클릭하면 check-email 요청
     */
    @GetMapping("/check-email")
    public String checkEmail(@CurrentUser Account account, Model model) {
        model.addAttribute("email",account.getEmail());
        return "account/check-email";
    }

    /**
     * resendConfirmEmail
     인증 메일을 다시 전송할 수 있는지 확인한
     보낼 수 있으면 전송, 첫페이지로 리다이렉트
     보낼 수 없으면 에러 메세지를 모델에 담아주고 이메일 확인 페이지 다시 보여주기
     */
    @GetMapping("/resend-confirm-email")
    public String resendConfirmEmail(@CurrentUser Account account, Model model) {
        // 이메일을 확인했는데, 보낼 수 없다.
        if (!account.canSendConfirmEmail()) {
            model.addAttribute("error", "인증 이메일은 1시간에 한번만 전송할 수 있습니다.");
            model.addAttribute("email",account.getEmail());
            return "account/check-email";
        }

        accountService.sendSignUpConfirmEmail(account);
        return "redirect:/";
    }
}
