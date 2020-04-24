package com.hobbytogther.hobby.validator;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hobbytogther.account.CurrentAccount;
import com.hobbytogther.domain.Account;
import com.hobbytogther.domain.Hobby;
import com.hobbytogther.domain.Tag;
import com.hobbytogther.domain.Zone;
import com.hobbytogther.hobby.form.HobbyDescriptionForm;
import com.hobbytogther.settings.TagService;
import com.hobbytogther.settings.form.TagForm;
import com.hobbytogther.tag.TagRepository;
import com.hobbytogther.zone.ZoneForm;
import com.hobbytogther.zone.ZoneRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/hobby/{path}/settings")
@RequiredArgsConstructor
public class HobbySettingsController {

    private final HobbyService hobbyService;
    private final ModelMapper modelMapper;
    private final TagService tagService;
    private final TagRepository tagRepository;
    private final ZoneRepository zoneRepository;
    private final ObjectMapper objectMapper;

    @GetMapping("/description")
    public String viewHobbySetting(@CurrentAccount Account account, @PathVariable String path, Model model) {
        Hobby hobby = hobbyService.getHobbyToUpdate(account, path);
        model.addAttribute(account);
        model.addAttribute(hobby);
        model.addAttribute(modelMapper.map(hobby, HobbyDescriptionForm.class));
        return "hobby/settings/description";
    }

    @PostMapping("/description")
    public String updateHobbyInfo(@CurrentAccount Account account, @PathVariable String path,
                                  @Valid HobbyDescriptionForm hobbyDescriptionForm, Errors errors,
                                  Model model, RedirectAttributes attributes) {
        Hobby hobby = hobbyService.getHobbyToUpdate(account, path);

        if (errors.hasErrors()) {
            model.addAttribute(account);
            model.addAttribute(hobby);
            return "hobby/settings/description";
        }

        hobbyService.updateHobbyDescription(hobby, hobbyDescriptionForm);
        attributes.addFlashAttribute("message", "Hobby 소개를 수정했습니다.");
        return "redirect:/hobby/" + getPath(path) + "/settings/description";
    }

    /** Banner */
    @GetMapping("/banner")
    public String hobbyImageForm(@CurrentAccount Account account, @PathVariable String path, Model model) {
        Hobby hobby = hobbyService.getHobbyToUpdate(account, path);
        model.addAttribute(account);
        model.addAttribute(hobby);
        return "hobby/settings/banner";
    }

    @PostMapping("/banner")
    public String hobbyImageSubmit(@CurrentAccount Account account, @PathVariable String path,
                                   String image, RedirectAttributes attributes) {
        Hobby hobby = hobbyService.getHobbyToUpdate(account, path);
        hobbyService.updateHobbyImage(hobby, image);
        attributes.addFlashAttribute("message", "hobby 이미지를 수정했습니다.");
        return "redirect:/hobby/" + getPath(path) + "/settings/banner";
    }

    @PostMapping("/banner/enable")
    public String enableHobbyBanner(@CurrentAccount Account account, @PathVariable String path) {
        Hobby hobby = hobbyService.getHobbyToUpdate(account, path);
        hobbyService.enableHobbyBanner(hobby);
        return "redirect:/hobby/" + getPath(path) + "/settings/banner";
    }

    @PostMapping("/banner/disable")
    public String disableHobbyBanner(@CurrentAccount Account account, @PathVariable String path) {
        Hobby hobby = hobbyService.getHobbyToUpdate(account, path);
        hobbyService.disableHobbyBanner(hobby);
        return "redirect:/hobby/" + getPath(path) + "/settings/banner";
    }

    private String getPath(String path) {
        return URLEncoder.encode(path, StandardCharsets.UTF_8);
    }


    @GetMapping("/tags")
    public String hobbyTagsForm(@CurrentAccount Account account, @PathVariable String path, Model model)
            throws JsonProcessingException {
        Hobby hobby = hobbyService.getHobbyToUpdate(account, path);
        model.addAttribute(account);
        model.addAttribute(hobby);

        model.addAttribute("tags", hobby.getTags().stream()
                .map(Tag::getTitle).collect(Collectors.toList()));
        List<String> allTagTitles = tagRepository.findAll().stream()
                .map(Tag::getTitle).collect(Collectors.toList());
        model.addAttribute("whitelist", objectMapper.writeValueAsString(allTagTitles));
        return "hobby/settings/tags";
    }

    @PostMapping("/tags/add")
    @ResponseBody
    public ResponseEntity addTag(@CurrentAccount Account account, @PathVariable String path,
                                 @RequestBody TagForm tagForm) {
        Hobby hobby = hobbyService.getHobbyToUpdateTag(account, path);
        Tag tag = tagService.findOrCreateNew(tagForm.getTagTitle());
        hobbyService.addTag(hobby, tag);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/tags/remove")
    @ResponseBody
    public ResponseEntity removeTag(@CurrentAccount Account account, @PathVariable String path,
                                    @RequestBody TagForm tagForm) {
        Hobby hobby = hobbyService.getHobbyToUpdateTag(account, path);
        Tag tag = tagRepository.findByTitle(tagForm.getTagTitle());
        if (tag == null) {
            return ResponseEntity.badRequest().build();
        }

        hobbyService.removeTag(hobby, tag);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/zones")
    public String hobbyZonesForm(@CurrentAccount Account account, @PathVariable String path, Model model)
            throws JsonProcessingException {
        Hobby hobby = hobbyService.getHobbyToUpdate(account, path);
        model.addAttribute(account);
        model.addAttribute(hobby);
        model.addAttribute("zones", hobby.getZones().stream()
                .map(Zone::toString).collect(Collectors.toList()));
        List<String> allZones = zoneRepository.findAll().stream().map(Zone::toString).collect(Collectors.toList());
        model.addAttribute("whitelist", objectMapper.writeValueAsString(allZones));
        return "hobby/settings/zones";
    }

    @PostMapping("/zones/add")
    @ResponseBody
    public ResponseEntity addZone(@CurrentAccount Account account, @PathVariable String path,
                                  @RequestBody ZoneForm zoneForm) {
        Hobby hobby = hobbyService.getHobbyToUpdateZone(account, path);
        Zone zone = zoneRepository.findByCityAndProvince(zoneForm.getCityName(), zoneForm.getProvinceName());
        if (zone == null) {
            return ResponseEntity.badRequest().build();
        }

        hobbyService.addZone(hobby, zone);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/zones/remove")
    @ResponseBody
    public ResponseEntity removeZone(@CurrentAccount Account account, @PathVariable String path,
                                     @RequestBody ZoneForm zoneForm) {
        Hobby hobby = hobbyService.getHobbyToUpdateZone(account, path);
        Zone zone = zoneRepository.findByCityAndProvince(zoneForm.getCityName(), zoneForm.getProvinceName());
        if (zone == null) {
            return ResponseEntity.badRequest().build();
        }

        hobbyService.removeZone(hobby, zone);
        return ResponseEntity.ok().build();
    }

    /** Hobby */
    @GetMapping("/hobby")
    public String hobbySettingForm(@CurrentAccount Account account, @PathVariable String path, Model model) {
        Hobby hobby = hobbyService.getHobbyToUpdate(account, path);
        model.addAttribute(account);
        model.addAttribute(hobby);
        return "hobby/settings/hobby";
    }

    @PostMapping("/hobby/publish")
    public String publishHobby(@CurrentAccount Account account, @PathVariable String path,
                               RedirectAttributes attributes) {
        Hobby hobby = hobbyService.getHobbyToUpdateStatus(account, path);
        hobbyService.publish(hobby);
        attributes.addFlashAttribute("message", "hobby를 공개했습니다.");
        return "redirect:/hobby/" + getPath(path) + "/settings/hobby";
    }

    @PostMapping("/hobby/close")
    public String closeHobby(@CurrentAccount Account account, @PathVariable String path,
                             RedirectAttributes attributes) {
        Hobby hobby = hobbyService.getHobbyToUpdateStatus(account, path);
        hobbyService.close(hobby);
        attributes.addFlashAttribute("message", "hobby를 종료했습니다.");
        return "redirect:/hobby/" + getPath(path) + "/settings/hobby";
    }

    @PostMapping("/recruit/start")
    public String startRecruit(@CurrentAccount Account account, @PathVariable String path, Model model,
                               RedirectAttributes attributes) {
        Hobby hobby = hobbyService.getHobbyToUpdateStatus(account, path);
        if (!hobby.canUpdateRecruiting()) {
            attributes.addFlashAttribute("message", "1시간 안에 인원 모집 설정을 여러번 변경할 수 없습니다.");
            return "redirect:/hobby/" + getPath(path) + "/settings/hobby";
        }

        hobbyService.startRecruit(hobby);
        attributes.addFlashAttribute("message", "인원 모집을 시작합니다.");
        return "redirect:/hobby/" + getPath(path) + "/settings/hobby";
    }

    @PostMapping("/recruit/stop")
    public String stopRecruit(@CurrentAccount Account account, @PathVariable String path, Model model,
                              RedirectAttributes attributes) {
        Hobby hobby = hobbyService.getHobbyToUpdate(account, path);
        if (!hobby.canUpdateRecruiting()) {
            attributes.addFlashAttribute("message", "1시간 안에 인원 모집 설정을 여러번 변경할 수 없습니다.");
            return "redirect:/hobby/" + getPath(path) + "/settings/hobby";
        }

        hobbyService.stopRecruit(hobby);
        attributes.addFlashAttribute("message", "인원 모집을 종료합니다.");
        return "redirect:/hobby/" + getPath(path) + "/settings/hobby";
    }

    /** Hobby Path*/
    @PostMapping("/hobby/path")
    public String updateHobbyPath(@CurrentAccount Account account, @PathVariable String path, Model model,
                                  String newPath, RedirectAttributes attributes) {

        Hobby hobby = hobbyService.getHobbyToUpdateStatus(account, path);
        if(!hobbyService.isValidPath(newPath)){
            model.addAttribute(account);
            model.addAttribute(hobby);
            model.addAttribute("hobbyPathError", " 해당 경로는 사용이 불가 합니다. 다른 값을 입력해 주시기바랍니다. ");

            return "hobby/settings/hobby";
        }

        hobbyService.updateHobbyPath(hobby,newPath);
        attributes.addFlashAttribute("message", "Hobby의 경로를 수정했습니다.");
        return "redirect:/hobby/" + getPath(newPath) + "/settings/hobby";

    }


}
