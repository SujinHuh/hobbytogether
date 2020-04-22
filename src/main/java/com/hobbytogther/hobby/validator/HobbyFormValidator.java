package com.hobbytogther.hobby.validator;

import com.hobbytogther.hobby.form.HobbyForm;
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
        HobbyForm hobbyForm = (HobbyForm)target;
        if (hobbyRepository.existsByPath(hobbyForm.getPath())) {
            errors.rejectValue("path", "wrong.path", "해당 Hobby 경로값을 사용할 수 없습니다.");
        }
    }
}
