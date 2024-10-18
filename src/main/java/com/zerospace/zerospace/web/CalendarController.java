package com.zerospace.zerospace.web;

import com.zerospace.zerospace.service.CalendarServiceImpl;
import com.zerospace.zerospace.service.MemberServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Controller
@ResponseBody
@Slf4j
@RequiredArgsConstructor
public class CalendarController {
    private final CalendarServiceImpl calendarService;
    private final MemberServiceImpl memberService;
    @PostMapping("/platform")
    public ResponseEntity<?> saveplatformInfo(HttpServletRequest request,
                                              @RequestBody Map<String, String> requestBody) {
        try {
            String platform = requestBody.get("platform");
            String email = requestBody.get("email");
            String password = requestBody.get("password");

            calendarService.saveHourplaceAccount(platform, email, password, request);
        } catch (Exception e) {
            return new ResponseEntity<>("fail", HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity("success", HttpStatus.OK);
    }

    @GetMapping("/data")
    public ResponseEntity<?> calendarConnection(HttpServletRequest request) {
        ResponseEntity<?> calendarInfo = calendarService.getCalendarInfo(request);
        log.info("return result = {}", calendarInfo.getBody());
        return calendarInfo;
    }

    @PostMapping("/month")
    public ResponseEntity<?> getCalendarByMonth(HttpServletRequest request,
                                                @RequestBody Map<String, Integer> requestBody) {
        int month = requestBody.get("month");
        int year = requestBody.get("year");

        // CalendarService에서 월별 데이터를 조회하고 반환
        ResponseEntity<?> response = calendarService.getCalendarInfoByMonth(request, month, year);
        return response;
    }

    @GetMapping("/memberleave")
    public ResponseEntity<?> memberLeave(HttpServletRequest request) {
        try {
            // 회원 탈퇴 및 데이터 삭제 로직 호출
            memberService.deleteUserData(request);
            return ResponseEntity.ok("회원 탈퇴 및 데이터 삭제가 완료되었습니다.");
        } catch (Exception e) {
            log.error("회원 탈퇴 중 오류 발생: {}", e.getMessage());
            return ResponseEntity.status(500).body("회원 탈퇴 처리 중 오류가 발생했습니다.");
        }
    }
}
