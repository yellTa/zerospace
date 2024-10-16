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
import org.springframework.web.bind.annotation.*;
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

    @GetMapping("/login/oauth2/code/kakao")
    public String oauth2Redirect(HttpServletResponse response, HttpServletRequest request){

        return request.getHeader(ACCESS_TOKEN_NAME);
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
