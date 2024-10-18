package com.zerospace.zerospace.web;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zerospace.zerospace.TestSecurityConfig;
import com.zerospace.zerospace.domain.CalendarInfo;
import com.zerospace.zerospace.service.CalendarServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Calendar;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureMockMvc
@Slf4j
@ContextConfiguration(classes = {TestSecurityConfig.class})  // 테스트 보안 설정 적용
class CalendarControllerTest {
    private final CalendarServiceImpl calendarService;

    @Autowired
    public CalendarControllerTest(CalendarServiceImpl calendarService) {
        this.calendarService = calendarService;
    }
    @Autowired
    private MockMvc mockMvc;


    @Test
    @DisplayName("month 조회 TEST")
    public void montSearch() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        ResponseEntity<?> calendarInfoByMonth = calendarService.getCalendarInfoByMonth(request, 9, 2024);
        String jsonString = calendarInfoByMonth.getBody().toString();

        log.info(jsonString);
    }


}
