package com.bookbook.booklink.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * Spring RestTemplate 빈(Bean) 설정을 위한 클래스입니다.
 * 이 클래스는 애플리케이션의 다른 서비스에서 HTTP 요청을 보낼 때 사용되는 RestTemplate 객체를 제공합니다.
 */
@Configuration
public class RestTemplateConfig {

    /**
     * RestTemplate 빈을 생성하고 등록합니다.
     * RestTemplate은 외부 API 호출을 위한 Spring의 동기식 HTTP 클라이언트입니다.
     *
     * @return RestTemplate 객체
     */
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
