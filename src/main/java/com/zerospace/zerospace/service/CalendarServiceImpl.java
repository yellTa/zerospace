package com.zerospace.zerospace.service;

import com.zerospace.zerospace.domain.HourplaceAccount;
import com.zerospace.zerospace.domain.SpacecloudAccount;
import com.zerospace.zerospace.exception.CrawlingException;
import com.zerospace.zerospace.exception.LoginFailedException;
import com.zerospace.zerospace.repository.HourplaceAccountRepository;
import com.zerospace.zerospace.repository.SpacecloudAccountRepository;
import com.zerospace.zerospace.service.utils.CrawlingLogic;
import com.zerospace.zerospace.service.utils.JWTTokenService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebDriver;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CalendarServiceImpl {
    private final JWTTokenService jwtTokenService;
    private final HourplaceAccountRepository hourplaceAccountRepository;
    private final SpacecloudAccountRepository spacecloudAccountRepository;
    private final CrawlingLogic crawlingLogic;

    @Transactional
    public void saveHourplaceAccount(String platform, String email, String password, HttpServletRequest request) {
        String accessToken = jwtTokenService.getAccessToken(request);
        String userId = jwtTokenService.getUserIdFromToken(accessToken);

        if (platform.contains("hourplace")) {
            HourplaceAccount hourplaceAccount = hourplaceAccountRepository.findByUserId(userId);
            if (hourplaceAccount != null) {
                hourplaceAccount.setHourplaceEmail(email);
                hourplaceAccount.setHourplacePassword(password);
            } else {
                hourplaceAccount = new HourplaceAccount(userId, email, password);
                hourplaceAccountRepository.save(hourplaceAccount);
            }

        } else if (platform.contains("spacecloud")) {
            SpacecloudAccount spacecloudAccount = spacecloudAccountRepository.findByUserId(userId);
            if (spacecloudAccount != null) {
                spacecloudAccount.setSpacecloudEmail(email);
                spacecloudAccount.setSpacecloudPassword(password);
            } else {
                spacecloudAccount = new SpacecloudAccount(userId, email, password);
                spacecloudAccountRepository.save(spacecloudAccount);
            }
        }


    }

    @Transactional
    public ResponseEntity<?> getCalendarInfo(HttpServletRequest request) {
//        String accessToken = jwtTokenService.getAccessToken(request);
//        String userId = jwtTokenService.getUserIdFromToken(accessToken);
        String userId = "testId";

        WebDriver driver = null;
        try {
            driver = crawlingLogic.loginCheck("hourplace", userId);
            if (driver == null) {
                log.info("hourplace 계정이 일차히지 않음");
                return new ResponseEntity<>("계정이 일치하지 않습니다", HttpStatus.UNAUTHORIZED);
            }
        } catch (LoginFailedException e) {
            log.info("hourplace 알 수 없는 에러 발생");
            return new ResponseEntity<>("알 수 없는 에러가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);

        }

        try {
            crawlingLogic.crawlingLogic("hourplace", driver);
        } catch (CrawlingException e) {
            return new ResponseEntity<>("알 수 없는 에러가 발생했습니다. 다시 시도해주세요", HttpStatus.INTERNAL_SERVER_ERROR);
        }


        driver = null;
        try {
            driver = crawlingLogic.loginCheck("spacecloud", userId);
            if (driver == null) {
                log.info("spacecloud 계정이 일차히지 않음");
                return new ResponseEntity<>("계정이 일치하지 않습니다", HttpStatus.UNAUTHORIZED);
            }
        } catch (LoginFailedException e) {
            log.info("spacecloud 알 수 없는 에러 발생");
            return new ResponseEntity<>("알 수 없는 에러가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        try {
            crawlingLogic.crawlingLogic("spacecloud", driver);
        } catch (CrawlingException e) {
            return new ResponseEntity<>("알 수 없는 에러가 발생했습니다. 다시 시도해주세요", HttpStatus.INTERNAL_SERVER_ERROR);
        }


        //날짜별 클릭수 저장하기
        //필요한 것- 저장할 Entity, 오늘의 날짜 정보, 저장할 Entity의 Repository

        return new ResponseEntity<>("success", HttpStatus.OK);
    }
}
