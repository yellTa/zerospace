package com.zerospace.zerospace.web;

import com.zerospace.zerospace.service.MemberServiceImpl;
import com.zerospace.zerospace.service.utils.JWTTokenService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.Map;

import static com.zerospace.zerospace.Const.Const.*;

@RestController
@RequiredArgsConstructor
@Slf4j
public class LoginController {
    private final OAuth2AuthorizedClientService authorizedClientService;
    private final JWTTokenService jwtTokenService;
    private final MemberServiceImpl memberService;

    @GetMapping("/loginResult")
    public Map<String,String> oauth2Redirect(HttpServletResponse response, HttpServletRequest request, @RequestParam String email, @RequestParam String userId){

        log.info("created AccessToken and Refresh Token");

        String accessToken = jwtTokenService.createAcecssToken(userId);
        String refreshToken = jwtTokenService.createRefreshToken(userId);

        response.setHeader(ACCESS_TOKEN_NAME, "Bearer " + accessToken);
        ResponseCookie refreshTokenCookie = ResponseCookie.from(REFRESH_TOKEN_NAME, refreshToken)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(REFRESH_TOKEN_VALIDITY_SECONDS)
                .sameSite("none")
                .build();
        response.addHeader("Set-Cookie", refreshTokenCookie.toString());

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        Map<String, String> emailMap = new LinkedHashMap<>();
        emailMap.put("email", email);

        return emailMap;
    }

    @GetMapping("apiTest")
    public String apiTest(HttpServletRequest request, HttpServletResponse response){
        String userId = request.getHeader(ACCESS_TOKEN_NAME);
        String email = memberService.getMemberEmailfromUserId(userId);
        return email;
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


    private String createUserId(){
        SecureRandom secureRandom = new SecureRandom();
        byte[] randomBytes = new byte[24];
        secureRandom.nextBytes(randomBytes);

        String userId =  Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
        return userId;
    }

    public String getKakaoAccessToken(String code) {
        String tokenUrl = "https://kauth.kakao.com/oauth/token";

        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", "카카오 클라이언트 ID");
        params.add("redirect_uri", "http://localhost:8080/loginResult");
        params.add("code", code);
        params.add("client_secret", "카카오 클라이언트 시크릿 (옵션)");

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

        ResponseEntity<Map> response = restTemplate.exchange(tokenUrl, HttpMethod.POST, request, Map.class);

        String accessToken = (String) response.getBody().get("access_token");
        return accessToken;
    }

    public Map<String, Object> getKakaoUserInfo(String accessToken) {
        String userInfoUrl = "https://kapi.kakao.com/v2/user/me";

        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);

        HttpEntity<String> request = new HttpEntity<>(headers);

        ResponseEntity<Map> response = restTemplate.exchange(userInfoUrl, HttpMethod.GET, request, Map.class);

        return response.getBody();
    }


}
