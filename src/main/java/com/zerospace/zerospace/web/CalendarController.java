package com.zerospace.zerospace.web;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/calendar")
@ResponseBody
@Slf4j
public class CalendarController {
    @PostMapping("/platform")
    public ResponseEntity saveplatformInfo(@RequestParam String platform, @RequestParam String password, @RequestParam String email) {
        log.info("platform date = {}", platform);
        log.info("platform date = {}", password);
        log.info("platform date = {}", email);


        return new ResponseEntity("hello", HttpStatus.OK);
    }

}
