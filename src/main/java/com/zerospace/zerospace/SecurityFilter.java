package com.zerospace.zerospace;


import com.zerospace.zerospace.filter.CORSFilter;
import com.zerospace.zerospace.filter.JwtAuthFilter;
import com.zerospace.zerospace.handler.OAuthSuccessHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.client.web.OAuth2LoginAuthenticationFilter;
import org.springframework.security.web.SecurityFilterChain;

@EnableWebSecurity
@Configuration
@Slf4j
public class SecurityFilter {
    private final JwtAuthFilter jwtAuthFilter;
    private final CORSFilter corsFilter;

    public SecurityFilter(JwtAuthFilter jwtAuthFilter, CORSFilter corsFilter) {
        this.jwtAuthFilter = jwtAuthFilter;
        this.corsFilter = corsFilter;
    }

    @Bean
    public SecurityFilterChain OAuthAndJWTValidationFilter(HttpSecurity httpSecurity, OAuthSuccessHandler successHandler) throws Exception {

        httpSecurity
                .addFilterBefore(corsFilter, OAuth2LoginAuthenticationFilter.class)
                .addFilterBefore(jwtAuthFilter, OAuth2LoginAuthenticationFilter.class)
                .authorizeRequests(authorize -> authorize
                        // 로그인 없이 접근 가능한 URI 설정
                        .requestMatchers("/calendar/data", "/resources/**", "/static/**", "/login", "/").permitAll()
                        // 그 외의 요청은 인증이 필요
                        .anyRequest().authenticated()
                )
                .oauth2Login(oauth2 -> oauth2
                        .loginPage("https://zzerospace.store:8080/oauth2/authorization/kakao")
                        .successHandler((request, response, authentication) -> {
                            response.sendRedirect("/loginResult");
                        })
                );
        return httpSecurity.build();
    }
}
