package com.zerospace.zerospace.service.utils;

import com.zerospace.zerospace.domain.HourplaceAccount;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;

@SpringBootTest
@Slf4j
class CrawlingLogicTest {

    @Autowired
    private HourplaceCrawling hourplaceCrawling;

    @Autowired
    private CrawlingLogic crawlingLogic;


    @Test

    void loginCheck() {
        WebDriver driver = hourplaceCrawling.hourplaceLogin("601atelier@naver.com", "Rhfdhkd1!!");
        if (driver == null) {
            log.info("login failed");
        }
    }

    @Test
    void loginCheck2() {
        ResponseEntity<?> responseEntity = crawlingLogic.loginCheck("hourplace", "testId");
        log.info(responseEntity.toString());
    }
}