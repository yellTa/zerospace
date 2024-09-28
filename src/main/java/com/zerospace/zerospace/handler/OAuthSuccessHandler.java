package com.zerospace.zerospace.handler;

import com.zerospace.zerospace.service.MemberService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.util.Map;

import java.io.IOException;

@Component
@Slf4j
@RequiredArgsConstructor
public class OAuthSuccessHandler implements AuthenticationSuccessHandler {

    private final MemberService memberService;
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        Map<String, Object> userAttributes = oAuth2User.getAttributes();

        String email ="";
        String nickName="";

        if(userAttributes.containsKey("kakao_account")){
            Map<String, Object> kakaoAccount = (Map<String, Object>) userAttributes.get("kakao_account");
            email = (String) kakaoAccount.get("email");
        }

        if(userAttributes.containsKey("properties")){
            Map<String, Object> kakaoAccount = (Map<String,Object>) userAttributes.get("properties");
            nickName = (String)kakaoAccount.get("nickname");
        }

        if(!memberService.hasMember(email)){
            memberService.join(email,nickName);
        }else{//위에는 회원 없을 때 회원 가입하는거
            //아래는 회원 있으니까 JWT토큰 발급하면 됨 근데 else 뺴고 WJT토
        }


    }
}
