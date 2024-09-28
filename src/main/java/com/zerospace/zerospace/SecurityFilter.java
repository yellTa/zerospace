package com.zerospace.zerospace;


import com.zerospace.zerospace.handler.OAuthSuccessHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@EnableWebSecurity
@Configuration
@Slf4j
public class SecurityFilter {

    @Bean
    public SecurityFilterChain OAuthAndJWTValidationFilter(HttpSecurity httpSecurity,OAuthSuccessHandler successHandler) throws Exception{
        log.info("start");
        httpSecurity
                .oauth2Login(oauth2 -> oauth2
                        .loginPage("/oauth2/authorization/kakao")
                        .successHandler(successHandler)
                );

        return httpSecurity.build();
    }
}
