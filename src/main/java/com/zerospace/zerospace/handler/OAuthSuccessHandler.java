package com.zerospace.zerospace.handler;

import com.zerospace.zerospace.service.MemberServiceImpl;
import com.zerospace.zerospace.service.utils.JWTTokenService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.Map;

import java.io.IOException;

@Component
@Slf4j
@RequiredArgsConstructor
public class OAuthSuccessHandler implements AuthenticationSuccessHandler {

    private final MemberServiceImpl memberService;
    private final JWTTokenService jwtTokenService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        Map<String, Object> userAttributes = oAuth2User.getAttributes();

        String email = "";
        String nickName = "";
        String userId = "";

        if (userAttributes.containsKey("kakao_account")) {
            Map<String, Object> kakaoAccount = (Map<String, Object>) userAttributes.get("kakao_account");
            email = (String) kakaoAccount.get("email");
            log.info("email = {} ", email);
        }

        if (userAttributes.containsKey("properties")) {
            Map<String, Object> kakaoAccount = (Map<String, Object>) userAttributes.get("properties");
            nickName = (String) kakaoAccount.get("nickname");
            log.info(nickName);
        }

        if (!memberService.hasMember(email)) {
            userId = createUserId();
            memberService.join(email, nickName, userId);
        } else {
            userId = memberService.getMemberuserId(email);
        }
        log.info("membersaveEND");

        response.sendRedirect("https://zero-space-service-snowy.vercel.app/oauth?userId=" + userId);
    }

    private String createUserId() {
        SecureRandom secureRandom = new SecureRandom();
        byte[] randomBytes = new byte[24];
        secureRandom.nextBytes(randomBytes);

        String userId = Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
        return userId;
    }
}
