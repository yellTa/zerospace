package com.zerospace.zerospace.service.utils;

import com.zerospace.zerospace.domain.HourplaceAccount;
import com.zerospace.zerospace.domain.SpacecloudAccount;
import com.zerospace.zerospace.repository.HourplaceAccountRepository;
import com.zerospace.zerospace.repository.SpacecloudAccountRepository;
import lombok.RequiredArgsConstructor;
import org.openqa.selenium.WebDriver;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CrawlingLogic {
    private final HourplaceAccountRepository hourplaceAccount;
    private final SpacecloudAccountRepository spacecloudAccount;
    private final HourplaceCrawling hourplaceCrawling;

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
                    return null;
                }
            } catch (Exception e) {

            }


        } else if (platform.equals("spacecloud")) {

        }


        return driver;
    }

}
