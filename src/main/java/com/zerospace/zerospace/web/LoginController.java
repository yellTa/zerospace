package com.zerospace.zerospace.web;

import com.zerospace.zerospace.service.MemberServiceImpl;
import com.zerospace.zerospace.service.utils.JWTTokenService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
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

    @PostMapping("/loginResult")
    public Map<String,String> oauth2Redirect(HttpServletResponse response, @RequestBody Map<String,String> bodyUserId){
        String userId = bodyUserId.get("userId");

        log.info("created AccessToken and Refresh Token");

        String accessToken = jwtTokenService.createAcecssToken(userId);
        String refreshToken = jwtTokenService.createRefreshToken(userId);
        String memberEmail = memberService.getMemberEmailfromUserId(userId);

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

        Map<String, String> map = new LinkedHashMap<>();
        map.put("email", memberEmail);
        map.put("AccessToken", accessToken);

        return map;
    }

    //Test후 삭제 예정
    @GetMapping("/apiTest")
    public String apiTest(HttpServletRequest request, HttpServletResponse response){
        String userId = request.getHeader(ACCESS_TOKEN_NAME);
        String email = memberService.getMemberEmailfromUserId(userId);
        return email;
    }
    @GetMapping("/logoutzero")
    public ResponseEntity<String> logout(HttpServletResponse response) {
        ResponseCookie refreshTokenCookie = ResponseCookie.from("refreshToken")
                .path("/")
                .sameSite("None")
                .httpOnly(true)
                .secure(true)
                .maxAge(0) // 즉시 만료
                .build();
        response.addHeader("Set-Cookie", refreshTokenCookie.toString());
        response.setHeader("Authorization", "");

        return new ResponseEntity("success",HttpStatus.OK);
    }
}
