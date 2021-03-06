package com.hobbytogther.modules.hobby.validator;

import com.hobbytogther.modules.account.Account;
import com.hobbytogther.modules.hobby.Hobby;
import com.hobbytogther.modules.hobby.event.HobbyCreatedEvent;
import com.hobbytogther.modules.hobby.event.HobbyUpdateEvent;
import com.hobbytogther.modules.tag.Tag;
import com.hobbytogther.modules.tag.TagRepository;
import com.hobbytogther.modules.zone.Zone;
import com.hobbytogther.modules.hobby.form.HobbyDescriptionForm;
import lombok.RequiredArgsConstructor;
import net.bytebuddy.utility.RandomString;
import org.modelmapper.ModelMapper;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

import java.util.HashSet;

import static com.hobbytogther.modules.hobby.form.HobbyForm.VALID_PATH;

@Service
@Transactional
@RequiredArgsConstructor
public class HobbyService {

    private final HobbyRepository hobbyRepository;
    private final ModelMapper modelMapper;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final TagRepository tagRepository;

    public Hobby createNewHobby(Hobby hobby, Account account) {
        Hobby newHobby = hobbyRepository.save(hobby);
        newHobby.addManager(account);
        return newHobby;
    }

    public Hobby getHobbyToUpdate(Account account, String path) {
        Hobby hobby = this.getHobby(path);
        checkIfManager(account, hobby);

        return hobby;
    }

    public Hobby getHobby(String path) {
        Hobby hobby = this.hobbyRepository.findByPath(path);
        checkIfExistingHobby(path, hobby);

        return hobby;
    }

    public void updateHobbyDescription(Hobby hobby, HobbyDescriptionForm hobbyDescriptionForm) {
        modelMapper.map(hobbyDescriptionForm, hobby);
        applicationEventPublisher.publishEvent(new HobbyUpdateEvent(hobby,"Hobby를 수정했습니다."));
    }

    public void updateHobbyImage(Hobby hobby, String image) {
        hobby.setImage(image);
    }

    public void enableHobbyBanner(Hobby hobby) {
        hobby.setUseBanner(true);
    }

    public void disableHobbyBanner(Hobby hobby) {
        hobby.setUseBanner(false);
    }
    public void addTag(Hobby hobby, Tag tag) {
        hobby.getTags().add(tag);
    }

    public void removeTag(Hobby hobby, Tag tag) {
        hobby.getTags().remove(tag);
    }

    public void addZone(Hobby hobby, Zone zone) {
        hobby.getZones().add(zone);
    }

    public void removeZone(Hobby hobby, Zone zone) {
        hobby.getZones().remove(zone);
    }

    public Hobby getHobbyToUpdateTag(Account account, String path) {
        Hobby hobby = hobbyRepository.findHobbyWithTagsByPath(path);
        checkIfExistingHobby(path, hobby);
        checkIfManager(account, hobby);
        return hobby;
    }

    public Hobby getHobbyToUpdateZone(Account account, String path) {
        Hobby hobby = hobbyRepository.findHobbyWithZonesByPath(path);
        checkIfExistingHobby(path, hobby);
        checkIfManager(account, hobby);
        return hobby;
    }
    public Hobby getHobbyToUpdateStatus(Account account, String path) {
        Hobby hobby = hobbyRepository.findHobbyWithManagersByPath(path);
        checkIfExistingHobby(path, hobby);
        checkIfManager(account, hobby);
        return hobby;
    }
    private void checkIfManager(Account account, Hobby hobby) {
        if (!hobby.isManagedBy(account)) {
            throw new AccessDeniedException("해당 기능을 사용할 수 없습니다.");
        }
    }

    private void checkIfExistingHobby(String path, Hobby hobby) {
        if (hobby == null) {
            throw new IllegalArgumentException(path + "에 해당하는 스터디가 없습니다.");
        }
    }
    public void publish(Hobby hobby) {
        hobby.publish();
        this.applicationEventPublisher.publishEvent(new HobbyCreatedEvent(hobby));
    }

    public void close(Hobby hobby) {
        hobby.close();
        applicationEventPublisher.publishEvent(new HobbyUpdateEvent(hobby,"Hobby를 종료했습니다."));
    }

    public void startRecruit(Hobby hobby) {
        hobby.startRecruit();
        applicationEventPublisher.publishEvent(new HobbyUpdateEvent(hobby,"팀원 모집을 시작했습니다."));
    }

    public void stopRecruit(Hobby hobby) {
        hobby.stopRecruit();
        applicationEventPublisher.publishEvent(new HobbyUpdateEvent(hobby,"팀원 모집을 중단했습니다."));
    }

    public void updateHobbyPath(Hobby hobby, String newPath) {
        hobby.setPath(newPath);
    }

    public boolean isValidPath(String newPath) {
        if(!newPath.matches(VALID_PATH)){
            return false;
        }
        return !hobbyRepository.existsByPath(newPath);
    }

    public boolean isValidTitle(String newTitle) {
       return newTitle.length() <= 50;
    }

    public void updateHobbyTitle(Hobby hobby, String newTitle) {
        hobby.setTitle(newTitle);
    }

    public void remove(Hobby hobby) {
        if(hobby.isRemovabel()) {
            hobbyRepository.delete(hobby);
        } else {
            throw new IllegalArgumentException("Hobby를  삭제할 수 없습니다. ");
        }
    }

    public void addMember(Hobby hobby, Account account) {
        hobby.addMember(account);
    }

    public void removeMember(Hobby hobby, Account account) {
        hobby.removeMember(account);
    }


    public Hobby getHobbyToEnroll(String path) {
         Hobby hobby = hobbyRepository.findHobbyOnlyByPath(path);
         checkIfExistingHobby(path,hobby);

         return hobby;
    }

    public void generateTestHobbies(Account account) {
        for(int i = 0; i <30 ; i++) {
            String randomvalue = RandomString.make(5);
            Hobby hobby = Hobby.builder()
                    .title("'테스트' 취미" + randomvalue)
                    .path("test-" + randomvalue)
                    .shortDescription("테스트용 Hobby 입니다.")
                    .fullDescription("test")
                    .tags(new HashSet<>())
                    .managers(new HashSet<>())
                    .build();

            hobby.publish();
            Hobby newHobby = this.createNewHobby(hobby, account);
            Tag jpa = tagRepository.findByTitle("JPA");
            newHobby.getTags().add(jpa);
        }

    }
}
