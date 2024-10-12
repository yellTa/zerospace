package com.zerospace.zerospace.web;

import com.zerospace.zerospace.service.CalendarServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/calendar")
@ResponseBody
@Slf4j
@RequiredArgsConstructor
public class CalendarController {
    private final CalendarServiceImpl calendarService;

    @PostMapping("/platform")
    public ResponseEntity saveplatformInfo(HttpServletRequest request,
                                           @RequestParam String platform,
                                           @RequestParam String password,
                                           @RequestParam String email) {
        calendarService.saveHourplaceAccount(platform, email, password, request);
        return new ResponseEntity("hello", HttpStatus.OK);
    }

}