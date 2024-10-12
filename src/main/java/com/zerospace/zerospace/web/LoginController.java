package com.zerospace.zerospace.web;

import com.zerospace.zerospace.service.JWTTokenService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Controller
@RequiredArgsConstructor
public class LoginController {
    private final OAuth2AuthorizedClientService authorizedClientService;
    private final JWTTokenService jwtTokenService;

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
