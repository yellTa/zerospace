package com.zerospace.zerospace;


import com.zerospace.zerospace.filter.JwtAuthFilter;
import com.zerospace.zerospace.handler.OAuthSuccessHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.client.web.OAuth2LoginAuthenticationFilter;
import org.springframework.security.web.SecurityFilterChain;

@EnableWebSecurity
@Configuration
@Slf4j
public class SecurityFilter {


    private final JwtAuthFilter jwtAuthFilter;

    public SecurityFilter(JwtAuthFilter jwtAuthFilter) {
        this.jwtAuthFilter = jwtAuthFilter;
    }

    @Bean
    public SecurityFilterChain OAuthAndJWTValidationFilter(HttpSecurity httpSecurity,OAuthSuccessHandler successHandler) throws Exception{
        httpSecurity
                .addFilterBefore(jwtAuthFilter, OAuth2LoginAuthenticationFilter.class)
                .oauth2Login(oauth2 -> oauth2
                        .loginPage("/oauth2/authorization/kakao")
                        .successHandler(successHandler)
                );
        return httpSecurity.build();
    }
}
