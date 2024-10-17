package com.zerospace.zerospace;


import com.zerospace.zerospace.filter.CORSFilter;
import com.zerospace.zerospace.filter.JwtAuthFilter;
import com.zerospace.zerospace.handler.OAuthSuccessHandler;
import com.zerospace.zerospace.service.utils.JWTTokenService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.client.web.OAuth2LoginAuthenticationFilter;
import org.springframework.security.web.SecurityFilterChain;

@EnableWebSecurity
@Configuration
@Slf4j
public class Config {


    @Autowired
    private final JWTTokenService jwtTokenService;

    public Config(JWTTokenService jwtTokenService) {
        this.jwtTokenService = jwtTokenService;
    }

    @Bean
    public SecurityFilterChain OAuthAndJWTValidationFilter(HttpSecurity httpSecurity, OAuthSuccessHandler successHandler) throws Exception {

        httpSecurity
                .csrf(csrf -> csrf.disable())
                .authorizeRequests(authorize -> authorize
                        // 로그인 없이 접근 가능한 URI 설정
                        .requestMatchers("/calendar/data", "/resources/**", "/static/**", "/loginResult", "/login", "/","/logout").permitAll()
                        // 그 외의 요청은 인증이 필요
                        .anyRequest().authenticated()
                )
                .addFilterBefore(new CORSFilter(), OAuth2LoginAuthenticationFilter.class)
                .addFilterBefore(new JwtAuthFilter(jwtTokenService), OAuth2LoginAuthenticationFilter.class)
                .oauth2Login(oauth2 -> oauth2
                        .loginPage("https://zzerospace.store:8443/oauth2/authorization/kakao")
                        .successHandler(successHandler)
                );
        return httpSecurity.build();
    }
}
