package com.hobbytogther.modules.account.validator;

import com.hobbytogther.modules.account.form.PasswordForm;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

/**
 * @Bean으로 등록할 필요없
 * PasswordFormValidator 다른 빈 사용하지 않아서, 'new' 로 사용 SettingsController 에서 formValidation 으로 등록해서 사용하면 됨
 */
public class PasswordFormValidator implements Validator {
    /** 어떤 타입에 폼객체를 검증할 것인가? - password 폼 타입의 할당가능한 타입이면 검증을 할 것이다.*/
    @Override
    public boolean supports(Class<?> aClass) {
        return PasswordForm.class.isAssignableFrom(aClass);
    }

    /** object 객체가 PasswordFrom 이된다. */
    @Override
    public void validate(Object object, Errors errors) {
        PasswordForm passwordForm = (PasswordForm)object;
        if(!passwordForm.getNewPassword().equals(passwordForm.getNewPasswordConfirm())) {
            errors.rejectValue("newPassword","wrong,value","입력한 새 패스워드가 일치하지 않습니다.");
        }
    }
}