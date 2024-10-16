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
    private final MemberService memberService;

    @GetMapping("/")
    @ResponseBody
    public String resposned(){
        return "ssss";
    }
    @GetMapping("apiTest")
    public String getUserInfoFromKakao(@RequestHeader("Authorization") String accessToken) {
        String kakaoUserInfoUrl = "https://kapi.kakao.com/v2/user/me";

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);  // AccessToken을 카카오에 전송
        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(kakaoUserInfoUrl, HttpMethod.GET, entity, String.class);

        return response.getBody();  // 카카오에서 반환한 사용자 정보
    }


    @GetMapping("/logout")
    public String logout(HttpSession session, @RequestHeader("Authorization") String accessToken) {
        // 카카오 로그아웃 API 호출
        String kakaoLogoutUrl = "https://kapi.kakao.com/v1/user/logout";

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();

        // Authorization 헤더에서 "Bearer " 부분을 제거하여 실제 토큰만 추출
        String token = accessToken.replace("Bearer ", "");
        headers.set("Authorization", "Bearer " + token);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        // 카카오 API 호출
        ResponseEntity<String> response = restTemplate.exchange(kakaoLogoutUrl, HttpMethod.POST, entity, String.class);

        if (response.getStatusCode().is2xxSuccessful()) {
            // 카카오 로그아웃 성공 시 백엔드 세션 종료
            session.invalidate();  // 현재 세션 무효화
            SecurityContextHolder.clearContext();  // Spring Security 세션 무효화

            // 로그아웃 성공 후 리다이렉트
            return "YYYYYYYYYYYYYYYYYYYYYES";
        } else {
            // 로그아웃 실패 시 에러 처리
            return "NOOOOOOOOOOOOOOOOOOOOOOOOOOO";
        }
    }
}
