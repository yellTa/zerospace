package com.zerospace.zerospace.service;

import com.zerospace.zerospace.domain.HourplaceAccount;
import com.zerospace.zerospace.domain.SpacecloudAccount;
import com.zerospace.zerospace.repository.HourplaceAccountRepository;
import com.zerospace.zerospace.repository.SpacecloudAccountRepository;
import com.zerospace.zerospace.service.utils.CrawlingLogic;
import com.zerospace.zerospace.service.utils.JWTTokenService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.openqa.selenium.WebDriver;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
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
        //사용자 계정정보 가져와서 로그인 수행하기
        //hourplace 사용자 정보 가져오기
        String accessToken = jwtTokenService.getAccessToken(request);
        String userId = jwtTokenService.getUserIdFromToken(accessToken);

        WebDriver hourplaceDriver = crawlingLogic.loginCheck("hourplace", userId);
        if (hourplaceDriver == null) {
            return new ResponseEntity<>("계정이 일치하지 않습니다", HttpStatus.UNAUTHORIZED);
        }
        //맞으면 hourplace 크롤링 수행


        //spacecloud 사용자 정보 가져오기
        WebDriver spacecloudDriver = crawlingLogic.loginCheck("spacecloud", userId);
        if (spacecloudDriver == null) {
            return new ResponseEntity<>("계정이 일치하지 않습니다", HttpStatus.UNAUTHORIZED);
        }

        // 로그인이 맞으면 크롤링 비교하기


        //날짜별 클릭수 저장하기
        //필요한 것- 저장할 Entity, 오늘의 날짜 정보, 저장할 Entity의 Repository

        return new ResponseEntity<>("success", HttpStatus.OK);
    }
}
