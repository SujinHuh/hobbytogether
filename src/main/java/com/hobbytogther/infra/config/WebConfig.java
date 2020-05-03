package com.hobbytogther.infra.config;

import com.hobbytogther.modules.notification.NotificationInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.StaticResourceLocation;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Configuration
/** SpringBoot에서 WebMVC를 커스터마이징하고 싶을 때,
 @EnableWebMvc 사용하면, springBoot가 제공하는 webMvc 자동설정하지 않겠다는 것
 @EnableWebMvc 사용하지 않은면, 기본설정에 + 추가설정만한다.
 */
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final NotificationInterceptor notificationInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        // 문자열로 변환
        List<String> staticResourcePath = Arrays.stream(StaticResourceLocation.values())
        .flatMap(StaticResourceLocation::getPatterns)
        .collect(Collectors.toList());
        staticResourcePath.add("/node_modules/**");

        registry.addInterceptor(notificationInterceptor)
        .excludePathPatterns(staticResourcePath); //제외할 패턴
    }
}
