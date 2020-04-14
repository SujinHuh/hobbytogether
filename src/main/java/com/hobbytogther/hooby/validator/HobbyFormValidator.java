package com.hobbytogther.hooby.validator;

import com.hobbytogther.hooby.HobbyRepository;
import com.hobbytogther.hooby.form.HobbyForm;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
@RequiredArgsConstructor
public class HobbyFormValidator implements Validator {

    private final HobbyRepository hobbyRepository;

    @Override
    public boolean supports(Class<?> clazz) {
        return HobbyForm.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        HobbyForm hobbyForm = (HobbyForm) target;
        if (hobbyRepository.existsByPath(hobbyForm.getPath())) { //true를return = 이미 있다면
            errors.rejectValue("path", "wrong.path", "취미 경로를 사용 할 수 없습니다.");
        }
    }
}
