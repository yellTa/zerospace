package com.zerospace.zerospace.service.utils;

import com.zerospace.zerospace.domain.CalendarInfo;
import com.zerospace.zerospace.exception.LoginFailedException;
import com.zerospace.zerospace.repository.HourplaceAccountRepository;
import com.zerospace.zerospace.repository.SpacecloudAccountRepository;
import com.zerospace.zerospace.service.utils.platformCrawling.HourplaceCrawling;
import com.zerospace.zerospace.service.utils.platformCrawling.SpacecloudCrawling;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebDriver;

import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
@RequiredArgsConstructor
@Slf4j
public class CrawlingLogic {
    private final HourplaceAccountRepository hourplaceAccount;
    private final SpacecloudAccountRepository spacecloudAccount;
    private final HourplaceCrawling hourplaceCrawling;
    private final SpacecloudCrawling spacecloudCrawling;

    public WebDriver loginCheck(String platform, String userId) {
        String id = "";
        String password = "";



        WebDriver driver = null;

        if (platform.equals("hourplace")) {
            //id pw가져오기
            id = hourplaceAccount.findByUserId(userId).getHourplaceEmail();
            password = hourplaceAccount.findByUserId(userId).getHourplacePassword();

            try {
                driver = hourplaceCrawling.hourplaceLogin(id, password);
                if (driver == null) {
                    log.info("hourplace login failed!");
                    return driver;
                }
            } catch (Exception e) {
                log.info(e.toString());
                throw new LoginFailedException("알 수 없는 에러가 발생했습니다.");
            }


        } else if (platform.equals("spacecloud")) {
            id = spacecloudAccount.findByUserId(userId).getSpacecloudEmail();
            password = spacecloudAccount.findByUserId(userId).getSpacecloudPassword();

            try {
                driver = spacecloudCrawling.spacecloudLogin(id, password);
                if (driver == null) {
                    log.info("spacecloud login failed!");
                    return null;
                }
            } catch (Exception e) {
                log.info(e.toString());
                throw new LoginFailedException("알 수 없는 에러가 발생했습니다.");
            }
        }

        return driver;
    }

    public ArrayList<CalendarInfo> crawlingLogic(String platform, WebDriver driver) {
        ArrayList<CalendarInfo> result = new ArrayList<>();
        if (platform.equals("hourplace")) {
            result = hourplaceCrawling.hourspaceGetInfo(driver);

        } else if (platform.equals("spacecloud")) {
            result = spacecloudCrawling.spacecloudGetInfo(driver);
        }

        return result;
    }

}
