package com.hobbytogther.account;

import com.hobbytogther.domain.Account;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;

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

        accountService.completeSignUp(account); //persistent 상태

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
    public String checkEmail(@CurrentAccount Account account, Model model) {
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
    public String resendConfirmEmail(@CurrentAccount Account account, Model model) {
        // 이메일을 확인했는데, 보낼 수 없다.
        if (!account.canSendConfirmEmail()) {
            model.addAttribute("error", "인증 이메일은 1시간에 한번만 전송할 수 있습니다.");
            model.addAttribute("email",account.getEmail());
            return "account/check-email";
        }

        accountService.sendSignUpConfirmEmail(account);
        return "redirect:/";
    }
    /**
     * 프로필
     * nickname으로 파싱, model에 유저정보 넣어주고,
     * 현재 User가 프로필에 주인인지 확인 @CurrentUser (현재 User정보) 요청을 보내는 사람이 누구인지 알아야 한다.
     * /nickanme과 일치하면 nickname에 해당하는 account와  조회를 하고있는 account가 일치한다면 - 조작 할 수 있는 권한을 가진 User 인것
     */
    @GetMapping("profile/{nickname}")
    public String viewProfile(@PathVariable String nickname, Model model, @CurrentAccount Account account) {
        Account byNickname = accountRepository.findByNickname(nickname);
        if(nickname == null) {
            throw new IllegalStateException(nickname + "해당하는 사용자가 없습니다.");
        }
        model.addAttribute(byNickname); //account 객체가 들어감
        model.addAttribute("isOwner",byNickname.equals(account));
        return "account/profile";
    }

    @GetMapping("/email-login")
    public String emailLoginForm() {
        return "account/email-login";
    }

    @PostMapping("/email-login")
    public String sendEmailLoginLink(String email, Model model, RedirectAttributes attributes) {
        Account account = accountRepository.findByEmail(email);
        if (account == null) {
            model.addAttribute("error", "유효한 이메일 주소가 아닙니다.");
            return "account/email-login";
        }

        if (!account.canSendConfirmEmail()) {
            model.addAttribute("error", "이메일 로그인은 1시간 뒤에 사용할 수 있습니다.");
//            return "account/email-login";
        }

        accountService.sendLoginLink(account);
        attributes.addFlashAttribute("message", "이메일 인증 메일을 발송했습니다.");
        return "redirect:/email-login";
    }

    @GetMapping("/login-by-email")
    public String loginByEmail(String token, String email, Model model) {
        Account account = accountRepository.findByEmail(email);
        String view = "account/logged-in-by-email";
        if (account == null || !account.isValidToken(token)) {
            model.addAttribute("error", "로그인할 수 없습니다.");
            return view;
        }

        accountService.login(account);
        return view;
    }
}
