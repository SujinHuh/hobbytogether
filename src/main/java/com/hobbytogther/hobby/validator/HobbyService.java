package com.hobbytogther.hobby.validator;

import com.hobbytogther.domain.Account;
import com.hobbytogther.domain.Hobby;
import com.hobbytogther.hobby.form.HobbyDescriptionForm;
import com.hobbytogther.hobby.validator.HobbyRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class HobbyService {

    private final HobbyRepository repository;
    private final ModelMapper modelMapper;

    public Hobby createNewHobby(Hobby hobby, Account account) {
        Hobby newHobby = repository.save(hobby);
        newHobby.addManager(account);
        return newHobby;
    }
    public Hobby getHobbyToUpdate(Account account, String path) {
        Hobby hobby = this.getHobby(path);
        if (!account.isManagerOf(hobby)) {
            throw new AccessDeniedException("해당 기능을 사용할 수 없습니다.");
        }

        return hobby;
    }

    public Hobby getHobby(String path) {
        Hobby hobby = this.repository.findByPath(path);
        if (hobby == null) {
            throw new IllegalArgumentException(path + "에 해당하는 스터디가 없습니다.");
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
