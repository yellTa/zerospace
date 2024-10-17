package com.zerospace.zerospace.service.utils;

import com.zerospace.zerospace.service.CalendarServiceImpl;
import com.zerospace.zerospace.service.utils.platformCrawling.HourplaceCrawling;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;

@SpringBootTest
@Slf4j
class CrawlingLogicTest {

    @Autowired
    private HourplaceCrawling hourplaceCrawling;

    @Autowired
    private CrawlingLogic crawlingLogic;

    @Autowired
    private CalendarServiceImpl calendarServiceImpl;


    @Test
    void loginCheck() {
        WebDriver driver = hourplaceCrawling.hourplaceLogin("601atelier@naver.com", "Rhfdhkd1!!");
        if (driver == null) {
            log.info("login failed");
        }
    }

    @Test
    void loginCheck2() {
        crawlingLogic.loginCheck("hourplace", "testId");

    }

    @Test
    public void lobinCheck3() {
        crawlingLogic.loginCheck("spacecloud", "testId");
    }

    @Test
    @DisplayName("calendser Servle Impl의 crawling 로직 수행")
    public void connectionTest() {
        MockHttpServletRequest request = new MockHttpServletRequest();

        try {
            ResponseEntity<?> calendarInfo = calendarServiceImpl.getCalendarInfo(request);
            log.info( calendarInfo.getBody().toString());
        } catch (Exception e) {
            log.info(e.toString());
        }
    }
}