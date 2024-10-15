package com.zerospace.zerospace.web;

import com.zerospace.zerospace.service.MemberService;
import com.zerospace.zerospace.service.utils.JWTTokenService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.Map;

import static com.zerospace.zerospace.Const.Const.*;

@RestController
@RequiredArgsConstructor
@Slf4j
public class LoginController {
    private final OAuth2AuthorizedClientService authorizedClientService;
    private final JWTTokenService jwtTokenService;
    private final MemberService memberService;

    @GetMapping("/loginResult")
    public String oauth2Redirect(HttpServletResponse response, HttpServletRequest request, Authentication authentication) {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        Map<String, Object> userAttributes = oAuth2User.getAttributes();

        String email = "";
        String nickName = "";
        String userId = "";

        if (userAttributes.containsKey("kakao_account")) {
            Map<String, Object> kakaoAccount = (Map<String, Object>) userAttributes.get("kakao_account");
            email = (String) kakaoAccount.get("email");
        }

        if (userAttributes.containsKey("properties")) {
            Map<String, Object> kakaoAccount = (Map<String, Object>) userAttributes.get("properties");
            nickName = (String) kakaoAccount.get("nickname");
        }

        if (!memberService.hasMember(email)) {
            userId = createUserId();
            memberService.join(email, nickName, userId);
        } else {
            userId = memberService.getMemberuserId(email);
        }
        String accessToken = "";
        String refreshToken = "";

        log.info("userId = {}", userId);
        try {
            accessToken = jwtTokenService.createAcecssToken(userId);
            refreshToken = jwtTokenService.createRefreshToken(userId);
        } catch (Exception e) {
            log.info(e.toString());
            return "failfailfailfail";
        }

        log.info("created AccessToken and Refresh Token");

        response.setHeader(ACCESS_TOKEN_NAME, "Bearer " + accessToken);
        ResponseCookie refreshTokenCookie = ResponseCookie.from(REFRESH_TOKEN_NAME, refreshToken)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(REFRESH_TOKEN_VALIDITY_SECONDS)
                .sameSite("none")
                .build();
        response.addHeader("Set-Cookie", refreshTokenCookie.toString());

        log.info("OAuth page ");
        String result = request.getHeader(ACCESS_TOKEN_NAME);
        return result;
    }

    private String createUserId() {
        SecureRandom secureRandom = new SecureRandom();
        byte[] randomBytes = new byte[24];
        secureRandom.nextBytes(randomBytes);

        String userId = Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
        return userId;
    }

    @GetMapping("/logout")
    public ResponseEntity<String> logout(HttpSession session, OAuth2AuthenticationToken authentication, HttpServletResponse response) {
        OAuth2AuthorizedClient authorizedClient = authorizedClientService.loadAuthorizedClient(
                authentication.getAuthorizedClientRegistrationId(),
                authentication.getName()
        );

        if (authentication != null) {
            String kakaoToken = authorizedClient.getAccessToken().getTokenValue();
            try {
                HttpHeaders headers = new HttpHeaders();
                headers.set("Authorization", "Bearer " + kakaoToken);

                HttpEntity<Void> request = new HttpEntity<>(headers);
                RestTemplate restTemplate = new RestTemplate();
                String kakaoLogoutUrl = "https://kapi.kakao.com/v1/user/logout";

                ResponseEntity<String> responseEntity = restTemplate.postForEntity(kakaoLogoutUrl, request, String.class);

                if (!responseEntity.getStatusCode().is2xxSuccessful()) {
                    return responseEntity;
                }

            } catch (HttpClientErrorException e) {
                e.printStackTrace();
            }
        }

        session.invalidate();
        SecurityContextHolder.clearContext();

        ResponseCookie refreshTokenCookie = ResponseCookie.from("refreshToken")
                .path("/")
                .sameSite("None")
                .httpOnly(true)
                .secure(true)
                .maxAge(0) // 즉시 만료
                .build();
        response.addHeader("Set-Cookie", refreshTokenCookie.toString());
        response.setHeader("Authorization", "");
        return ResponseEntity.status(HttpStatus.FOUND)
                .header("Location", "http://localhost:8080/")
                .build();
    }
}
