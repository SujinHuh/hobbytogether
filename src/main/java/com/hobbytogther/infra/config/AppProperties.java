package com.hobbytogther.infra.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties("app") //Prefix : app  해당 바인딩
public class AppProperties {
    /** "application.properties" 선언해좋은 'app.host'에 값을 AppProperties 에 값을 받을 수 있고, 다른곳 (AccountService)애서 사용 가능하다.*/
    private String host; //host를 바인딩 받겠다.
}
