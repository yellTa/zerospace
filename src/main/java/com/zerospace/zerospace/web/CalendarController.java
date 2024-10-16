package com.zerospace.zerospace.web;

import com.zerospace.zerospace.service.CalendarServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Controller
@RequestMapping("/calendar")
@ResponseBody
@Slf4j
@RequiredArgsConstructor
public class CalendarController {
    private final CalendarServiceImpl calendarService;
    @PostMapping("/platform")
    public ResponseEntity<?> saveplatformInfo(HttpServletRequest request,
                                              @RequestParam String platform,
                                              @RequestParam String password,
                                              @RequestParam String email) {
        try {
            calendarService.saveHourplaceAccount(platform, email, password, request);
        } catch (Exception e) {
            return new ResponseEntity<>("fail", HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity("success", HttpStatus.OK);
    }

    @GetMapping("/data")
    public ResponseEntity<?> calendarConnection(HttpServletRequest request) {
        Authentication authentication = (Authentication) request.getUserPrincipal();
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

        String email = "";

        if (oAuth2User.getAttributes().containsKey("kakao_account")) {
            Map<String, Object> kakaoAccount = (Map<String, Object>) oAuth2User.getAttributes().get("kakao_account");
            email = (String) kakaoAccount.get("email");
        }

        calendarService.getCalendarInfo(request);
        log.info("who are you ? = {}", email);
        return new ResponseEntity("hello", HttpStatus.OK);
    }
}
