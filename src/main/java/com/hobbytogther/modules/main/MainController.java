package com.hobbytogther.modules.main;

import com.hobbytogther.modules.account.AccountRepository;
import com.hobbytogther.modules.account.CurrentAccount;
import com.hobbytogther.modules.account.Account;
import com.hobbytogther.modules.event.EnrollmentRepository;
import com.hobbytogther.modules.hobby.Hobby;
import com.hobbytogther.modules.hobby.validator.HobbyRepository;
import com.hobbytogther.modules.notification.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class MainController {

    private final HobbyRepository hobbyRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final AccountRepository accountRepository;

    @GetMapping("/")
    public String home(@CurrentAccount Account account, Model model){
        if(account != null) { //인증을 한 사용자
            Account accountLoaded = accountRepository.findAccountWithTagsAndZonesById(account.getId());
            model.addAttribute(accountLoaded);
            model.addAttribute("enrollmentList", enrollmentRepository.findByAccountAndAcceptedOrderByEnrolledAtDesc(account, true));
            model.addAttribute("hobbyList", hobbyRepository.findByAccount(
                    accountLoaded.getTags(),
                    accountLoaded.getZones()));
            model.addAttribute("hobbyManagerOf",
                    hobbyRepository.findFirst5ByManagersContainingAndClosedOrderByPublishedDateTimeDesc(account, false));
            model.addAttribute("hobbyMemberOf",
                    hobbyRepository.findFirst5ByMembersContainingAndClosedOrderByPublishedDateTimeDesc(account, false));
            return "index-after-login";
        }
        //view 에서는 null인지 아닌지 확인

        model.addAttribute("hobbyList", hobbyRepository.findFirst9ByPublishedAndClosedOrderByPublishedDateTimeDesc(true, false));

        return "index";
    }
    /**
     * login 처리
     */
    @GetMapping("/login")
    public String login() {
        return "login";
    }

    /** Search Hobby */
    @GetMapping("/search/hobby")
    public String searchHobby(@PageableDefault(size = 9,sort = "publishedDateTime", direction = Sort.Direction.ASC)
                                          Pageable pageable, String keyword, Model model) {
        //querydsl 사용 - 확장 구현체
        Page<Hobby> hobbyPage = hobbyRepository.findByKeyword(keyword, pageable);
        model.addAttribute("hobbyPage",hobbyPage);
        model.addAttribute("keyword", keyword);
        model.addAttribute("sortProperty",
                pageable.getSort().toString().contains("publishedDateTime") ? "publishedDateTime" : "memberCount");

        return "search";
    }
}

