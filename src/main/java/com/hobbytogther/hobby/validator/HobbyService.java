package com.hobbytogther.hobby.validator;

import com.hobbytogther.domain.Account;
import com.hobbytogther.domain.Hobby;
import com.hobbytogther.domain.Tag;
import com.hobbytogther.domain.Zone;
import com.hobbytogther.hobby.form.HobbyDescriptionForm;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import javax.xml.validation.Validator;

import static com.hobbytogther.hobby.form.HobbyForm.VALID_PATH;

@Service
@Transactional
@RequiredArgsConstructor
public class HobbyService {

    private final HobbyRepository hobbyRepository;
    private final ModelMapper modelMapper;

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
        if (!account.isManagerOf(hobby)) {
            throw new AccessDeniedException("해당 기능을 사용할 수 없습니다.");
        }
    }

    private void checkIfExistingHobby(String path, Hobby hobby) {
        if (hobby == null) {
            throw new IllegalArgumentException(path + "에 해당하는 스터디가 없습니다.");
        }
    }
    public void publish(Hobby study) {
        study.publish();
    }

    public void close(Hobby study) {
        study.close();
    }

    public void startRecruit(Hobby study) {
        study.startRecruit();
    }

    public void stopRecruit(Hobby study) {
        study.stopRecruit();
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
}
