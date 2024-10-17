package com.zerospace.zerospace.web;

import com.zerospace.zerospace.service.CalendarServiceImpl;
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
}
