package com.hobbytogther.settings;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hobbytogther.account.AccountService;
import com.hobbytogther.account.CurrentAccount;
import com.hobbytogther.domain.Account;
import com.hobbytogther.domain.Tag;
import com.hobbytogther.domain.Zone;
import com.hobbytogther.settings.form.*;
import com.hobbytogther.settings.validator.NicknameValidator;
import com.hobbytogther.settings.validator.Notifications;
import com.hobbytogther.settings.validator.PasswordFormValidator;
import com.hobbytogther.tag.TagRepository;
import com.hobbytogther.zone.ZoneRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import static com.hobbytogther.settings.SettingsController.ROOT;
import static com.hobbytogther.settings.SettingsController.SETTINGS;
import javax.validation.Valid;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
@RequestMapping(ROOT + SETTINGS)
@RequiredArgsConstructor
public class SettingsController {


    static final String ROOT = "/";
    static final String SETTINGS = "settings";
    static final String PROFILE = "/profile";
    static final String PASSWORD = "/password";
    static final String NOTIFICATIONS = "/notifications";
    static final String ACCOUNT = "/account";
    static final String TAGS = "/tags";
    static final String ZONES = "/zones";

    private final AccountService accountService;
    private final NicknameValidator nicknameValidator;
    private final ModelMapper modelMapper;
    private final TagRepository tagRepository;
    private final ObjectMapper objectMapper;
    private final ZoneRepository zoneRepository;

    @InitBinder("passwordForm")
    public void initBinder(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(new PasswordFormValidator());
    }
    @InitBinder("nicknameForm")
    public void nicknameFormInitBinder(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(nicknameValidator);
    }


    /**현재 사용자 정보 넣어줌*/
    @GetMapping(PROFILE)
    public String updateProfileForm(@CurrentAccount Account account, Model model) {
        model.addAttribute(account);
        model.addAttribute(modelMapper.map(account, Profile.class)); /** sauce에 account에 들어있는 data로 Profile.class 채워주는것 */
        return SETTINGS + PROFILE;
    }

    /**현재 사용자 정보 수정*/
    @PostMapping(PROFILE)
    //errors가 (ModelAttribute)바인딩 에러를 받아주는 폼 애트리 뷰트 객체 오른쪽에 두어야 한다. (ModelAttribute)생략 가능
    public String updateProfile(@CurrentAccount Account account, @Valid @ModelAttribute Profile profile, Errors errors,
                                Model model, RedirectAttributes attributes) {
        if (errors.hasErrors()) {
            model.addAttribute(account);
            return SETTINGS + PROFILE;
        }
        accountService.updateProfile(account,profile);
        //잠깐 한번쓸 데이터 RedirectAttributes 한번 사용하고 없어짐
        attributes.addFlashAttribute("message", "프로필 수정 완료");
        return "redirect:/" + SETTINGS + PROFILE;
    }

    /** PASSWORD FormValidator 를 사용해서 판별 */
    @GetMapping(PASSWORD)
    public String updatePasswordForm(@CurrentAccount Account account, Model model) {
        model.addAttribute(account);
        model.addAttribute(new PasswordForm());
        return SETTINGS + PASSWORD;
    }

    /**Update하려면 현재 접속해있는 사용자*/
    @PostMapping(PASSWORD)
    public String updatePassword(@CurrentAccount Account account, @Valid PasswordForm passwordForm, Errors errors,
                                 Model model, RedirectAttributes attributes) {
        if(errors.hasErrors()) {
            model.addAttribute(account);
            return SETTINGS + PASSWORD;
        }
        accountService.updatePassword(account, passwordForm.getNewPassword());
        attributes.addFlashAttribute("message" ,"패스워드를 변경했습니다.");
        return "redirect:/" + SETTINGS + PASSWORD;
    }

    @GetMapping(NOTIFICATIONS)
    public String updateNotificationsForm(@CurrentAccount Account account, Model model) {
        model.addAttribute(account);
        model.addAttribute(modelMapper.map(account,Notifications.class));
        return SETTINGS + NOTIFICATIONS;
    }

    @PostMapping(NOTIFICATIONS)
    public String updateNotifications(@CurrentAccount Account account, @Valid Notifications notifications, Errors errors,
                                      Model model, RedirectAttributes attributes) {
        if (errors.hasErrors()) {
            model.addAttribute(account);
            return SETTINGS + NOTIFICATIONS;
        }

        accountService.updateNotifications(account, notifications);
        attributes.addFlashAttribute("message", "알림 설정을 변경했습니다.");
        return "redirect:/" + SETTINGS + NOTIFICATIONS;
    }
    @GetMapping(ACCOUNT)
    public String updateAccountForm(@CurrentAccount Account account, Model model) {
        model.addAttribute(account);
        model.addAttribute(modelMapper.map(account, NicknameForm.class));
        return SETTINGS + ACCOUNT;
    }

    @PostMapping(ACCOUNT)
    public String updateAccount(@CurrentAccount Account account, @Valid NicknameForm nicknameForm, Errors errors,
                                Model model, RedirectAttributes attributes) {
        if (errors.hasErrors()) {
            model.addAttribute(account);
            return SETTINGS + ACCOUNT;
        }

        accountService.updateNickname(account, nicknameForm.getNickname());
        attributes.addFlashAttribute("message", "닉네임을 수정했습니다.");
        return "redirect:/" + SETTINGS + ACCOUNT;
    }


    /** Tag URL - updateTags */
    @GetMapping(TAGS)
    public String updateTags(@CurrentAccount Account account, Model model) throws JsonProcessingException {
        model.addAttribute(account);
        //Tag 정보조회
        //문자열 타입의 List
        Set<Tag> tags = accountService.getTags(account);
        /*현재 User가 들고 있는 tag 전달*/       /** tag stream을 map으로 안에 들어있는 tag들을 tagtitle만 가져옴 : tag가 문자열로 바뀌는것 문자열을 수집해서 List로 변환해서 전달 */
        model.addAttribute("tags",tags.stream().map(Tag::getTitle).collect(Collectors.toList()));
        /** Tag 목록을 화이트 리스트로 제공해야함
         stream으로 맵핑을 해서 타입이 tag인데, StringType(tag 이름)으로 변환 List로 변환 하면 alltags (전체태그)가 나옴*/
        List<String> allTags = tagRepository.findAll().stream().map(Tag::getTitle).collect(Collectors.toList());
        /** objectMapper - alltags를 jason으로 변환해서 내보냄*/
        model.addAttribute("whitelist", objectMapper.writeValueAsString(allTags));
        return SETTINGS + TAGS;
    }


    /** Tag URL - addTag */
    @PostMapping(TAGS + "/add")         //요청이 본문에 들어옴 @RequestBody - TagForm을 받아서
    @ResponseBody /** AJAX 요청 응답 자체가 ResponseBody 되어야 한다. 반환 값은 ResponseEntity */
    public ResponseEntity addTag(@CurrentAccount Account account, @RequestBody TagForm tagForm) {
        String title = tagForm.getTagTitle();
         Tag tag = tagRepository.findByTitle(title);

         if(tag == null) {
             tag = tagRepository.save(Tag.builder().title(tagForm.getTagTitle()).build());
         }
         accountService.addTag(account,tag);
        return ResponseEntity.ok().build();
        /**        /**Optional
         Tag tag = tagRepository.findByTitle(title) // 태그 파일에서 찾아보고 없으면
         .orElseGet(() -> tagRepository.save(Tag.builder() //타이틀에 해당하는 것을 저장해서
         .title(tagForm.getTagTitle())// 받아온다.
         .build())); */

    }

    /** Tag URL - removeTag */
    @PostMapping( TAGS + "/remove")
    @ResponseBody /** AJAX 요청 응답 자체가 ResponseBody 되어야 한다. 반환 값은 ResponseEntity */
    public ResponseEntity removeTag(@CurrentAccount Account account, @RequestBody TagForm tagForm){
        String title = tagForm.getTagTitle();
        Tag tag = tagRepository.findByTitle(title);
        if(tag == null) {
            return ResponseEntity.badRequest().build(); //tag가 없는 tag를 삭제
        }
        accountService.removeTag(account,tag);
        return ResponseEntity.ok().build();
    }

    /** Zones URL - updateZonesForm*/
    @GetMapping(ZONES)
    public String updateZonesForm(@CurrentAccount Account account, Model model) throws JsonProcessingException {
        model.addAttribute(account);

        Set<Zone> zones = accountService.getZones(account);
        model.addAttribute("zones", zones.stream().map(Zone::toString).collect(Collectors.toList()));

        List<String> allZones = zoneRepository.findAll().stream().map(Zone::toString).collect(Collectors.toList());
        model.addAttribute("whitelist", objectMapper.writeValueAsString(allZones));

        return SETTINGS + ZONES;
    }

    @PostMapping(ZONES + "/add")
    @ResponseBody
    public ResponseEntity addZone(@CurrentAccount Account account, @RequestBody ZoneForm zoneForm) {
        Zone zone = zoneRepository.findByCityAndProvince(zoneForm.getCityName(), zoneForm.getProvinceName());
        if (zone == null) {
            return ResponseEntity.badRequest().build();
        }

        accountService.addZone(account, zone);
        return ResponseEntity.ok().build();
    }

    @PostMapping(ZONES + "/remove")
    @ResponseBody
    public ResponseEntity removeZone(@CurrentAccount Account account, @RequestBody ZoneForm zoneForm) {
        Zone zone = zoneRepository.findByCityAndProvince(zoneForm.getCityName(), zoneForm.getProvinceName());
        if (zone == null) {
            return ResponseEntity.badRequest().build();
        }

        accountService.removeZone(account, zone);
        return ResponseEntity.ok().build();
    }

}
