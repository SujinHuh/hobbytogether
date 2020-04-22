package com.hobbytogther.hooby;

import com.hobbytogther.domain.Account;
import com.hobbytogther.domain.Hobby;
import com.hobbytogther.domain.Tag;
import com.hobbytogther.domain.Zone;
import com.hobbytogther.hooby.form.HobbyDescriptionForm;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.hobbytogther.hooby.form.HobbyForm.VALID_PATH_PATTERN;

@Service
@Transactional
@RequiredArgsConstructor
public class HobbyService {

    private final HobbyRepository hobbyRepository;
    private final ModelMapper modelMapper;

    public Hobby createNewHobby(Hobby hobby, Account account) {
        Hobby newHobby = hobbyRepository.save(hobby);
        newHobby.addManager(account);
        return hobby;
    }

    public Hobby getHobby(String path) {
        Hobby hobby = this.hobbyRepository.findByPath(path);
        if(hobby == null) {
            throw new IllegalArgumentException(path + "에 해당하는 hpbby 없습니다.");
        }
        return hobby;
    }
    public Hobby getHobbyToUpdate(Account account, String path) {
        Hobby hobby = this.getHobby(path);
        if (!account.isManagerOf(hobby)) {
            throw new AccessDeniedException("해당 기능을 사용할 수 없습니다.");// bad request
        }
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
            throw new IllegalArgumentException(path + "에 해당하는 hobby 없습니다.");
        }
    }

    public void publish(Hobby hobby) {
        hobby.publish();
    }
    public void close(Hobby hobby) {
        hobby.close();
    }

    public void startRecruit(Hobby hobby) {
        hobby.startRecruit();
    }

    public void stopRecruit(Hobby hobby) {
        hobby.stopRecruit();
    }

    public boolean isValidPath(String newPath) {
        if(!newPath.matches(VALID_PATH_PATTERN)) {
            return false;
        }
        return !hobbyRepository.existsByPath(newPath);
    }

    public void updateHobbyPath(Hobby hobby, String newPath) {
        hobby.setPath(newPath);
    }

    public boolean isValidTitle(String newTitle) {
        return newTitle.length() <= 50;
    }

    public void updateHobbyTitle(Hobby hobby, String newTitle) {
        hobby.setTitle(newTitle);
    }

    public void remove(Hobby hobby) {
        if(hobby.isRemovable()) {
            hobbyRepository.delete(hobby);
        }
        else {
            throw new IllegalArgumentException("Hobby를 삭제할 수 없습니다.");
        }

    }

    public void addMember(Hobby hobby, Account account) {
        hobby.addMember(account);
    }

    public void removeMember(Hobby hobby, Account account) {
        hobby.removeMember(account);
    }
}
