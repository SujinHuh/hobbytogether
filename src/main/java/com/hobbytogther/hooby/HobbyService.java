package com.hobbytogther.hooby;

import com.hobbytogther.domain.Account;
import com.hobbytogther.domain.Hobby;
import com.hobbytogther.hooby.form.HobbyDescriptionForm;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    public Hobby getHoby(String path) {
        Hobby hobby = hobbyRepository.findByPath(path);
        if(hobby == null) {
            throw new IllegalArgumentException(path + "에 해당하는 스터디가 없습니다.");
        }
        return hobby;
    }
    public Hobby getHobbyToUpdate(Account account, String path) {
        Hobby hobby = this.getHoby(path);
        if (!account.isManagerOf(hobby)) {
            throw new AccessDeniedException("해당 기능을 사용할 수 없습니다.");
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
}
