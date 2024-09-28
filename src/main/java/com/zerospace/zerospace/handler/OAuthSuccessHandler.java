package com.zerospace.zerospace.handler;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.util.Map;

import java.io.IOException;

@Component
@Slf4j
public class OAuthSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

        Map<String, Object> userAttributes = oAuth2User.getAttributes();

        if(userAttributes.containsKey("kakao_account")){
            Map<String, Object> kakaoAccount = (Map<String, Object>) userAttributes.get("kakao_account");
            log.info("email {}",  kakaoAccount.get("email"));
        }

        if(userAttributes.containsKey("properties")){
            Map<String, Object> kakaoAccount = (Map<String,Object>) userAttributes.get("properties");
            log.info("nickname = {}", kakaoAccount.get("nickname"));
        }
    }
}
