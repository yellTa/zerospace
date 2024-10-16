package com.zerospace.zerospace.handler;

import com.zerospace.zerospace.service.MemberService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.net.URLEncoder;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Map;

import java.io.IOException;

@Component
@Slf4j
@RequiredArgsConstructor
public class OAuthSuccessHandler implements AuthenticationSuccessHandler {

    private final MemberService memberService;
    private final OAuth2AuthorizedClientService authorizedClientService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        log.info("success Handler start");
        OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
        String clientRegistrationId = oauthToken.getAuthorizedClientRegistrationId();

        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        Map<String, Object> userAttributes = oAuth2User.getAttributes();

        String email = "";
        String nickName = "";
        String userId = "";

        if (userAttributes.containsKey("kakao_account")) {
            Map<String, Object> kakaoAccount = (Map<String, Object>) userAttributes.get("kakao_account");
            email = (String) kakaoAccount.get("email");
            log.info("email={}", email);
        }

        if (userAttributes.containsKey("properties")) {
            Map<String, Object> kakaoAccount = (Map<String, Object>) userAttributes.get("properties");
            nickName = (String) kakaoAccount.get("nickname");
            log.info("nickName ={}", nickName);
        }

        if (!memberService.hasMember(email)) {
            userId = createUserId();
            memberService.join(email, nickName, userId);
        } else {
            userId = memberService.getMemberuserId(email);
        }


        String kakaoAccessToken = "";
        try {
            OAuth2AuthorizedClient authorizedClient = authorizedClientService.loadAuthorizedClient(
                    clientRegistrationId,
                    authentication.getName()
            );
            kakaoAccessToken = authorizedClient.getAccessToken().getTokenValue();
            log.info("Kakao Access Token: {}", kakaoAccessToken);
        } catch (Exception e) {
            log.info(e.toString());
        }

        log.info("created UserId ={}", userId);

        String redirectUrl = "https://zero-space-service-fkbbrm78r-p-inns-projects.vercel.app/main/calendar";
        String queryParams = "?Authorization=Bearer " + URLEncoder.encode(kakaoAccessToken, "UTF-8") + "&userId=" + URLEncoder.encode(userId, "UTF-8") +"&email=" + URLEncoder.encode(email, "UTF-8");

        response.sendRedirect(redirectUrl + queryParams);
    }

    private String createUserId() {
        SecureRandom secureRandom = new SecureRandom();
        byte[] randomBytes = new byte[24];
        secureRandom.nextBytes(randomBytes);

        String userId = Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
        return userId;
    }
}
