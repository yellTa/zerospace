package com.zerospace.zerospace.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zerospace.zerospace.domain.CalendarInfo;
import com.zerospace.zerospace.domain.HourplaceAccount;
import com.zerospace.zerospace.domain.SpacecloudAccount;
import com.zerospace.zerospace.exception.CrawlingException;
import com.zerospace.zerospace.exception.LoginFailedException;
import com.zerospace.zerospace.repository.CalendarInfoRepository;
import com.zerospace.zerospace.repository.HourplaceAccountRepository;
import com.zerospace.zerospace.repository.SpacecloudAccountRepository;
import com.zerospace.zerospace.service.utils.ClickRateCount;
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

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class CalendarServiceImpl {
    private final JWTTokenService jwtTokenService;
    private final HourplaceAccountRepository hourplaceAccountRepository;
    private final SpacecloudAccountRepository spacecloudAccountRepository;
    private final CrawlingLogic crawlingLogic;
    private final CalendarInfoRepository calendarInfoRepository;
    private final ClickRateCount clickRateCount;

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
        String userId = "testId"; //test용 계정
        WebDriver driver = null;

        ArrayList<CalendarInfo> hourplaceInfo = new ArrayList<>();
        ArrayList<CalendarInfo> spacecloud = new ArrayList<>();

        try {
            boolean flag = true;
            driver = crawlingLogic.loginCheck("hourplace", userId);
            if (driver == null) {
                log.info("hourplace 계정이 일치하지 않음");
            }else{
                try {
                    log.info("hourplace crawling start");
                    hourplaceInfo = crawlingLogic.crawlingLogic("hourplace",userId, driver);
                } catch (CrawlingException e) {
                    return new ResponseEntity<>("알 수 없는 에러가 발생했습니다. 다시 시도해주세요", HttpStatus.INTERNAL_SERVER_ERROR);
                }
            }
        } catch (LoginFailedException e) {
            log.info("hourplace 알 수 없는 에러 발생");
            return new ResponseEntity<>("알 수 없는 에러가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        driver = null;

        try {
            log.info("spacecloud logincheck start");
            driver = crawlingLogic.loginCheck("spacecloud", userId);
            if (driver == null) {
                log.info("spacecloud 계정이 일치하지 않음");
            }else{
                try {
                    log.info("hourplace crawling start");
                    spacecloud = crawlingLogic.crawlingLogic("spacecloud",userId,driver);
                } catch (CrawlingException e) {
                    return new ResponseEntity<>("알 수 없는 에러가 발생했습니다. 다시 시도해주세요", HttpStatus.INTERNAL_SERVER_ERROR);
                }
            }
        } catch (LoginFailedException e) {
            log.info("spacecloud 알 수 없는 에러 발생");
            return new ResponseEntity<>("알 수 없는 에러가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        ArrayList<CalendarInfo> crawlingResult = new ArrayList<>();

        crawlingResult.addAll(hourplaceInfo);
        crawlingResult.addAll(spacecloud);

        //DB에 넣는 작업 수행하기
        try {
            calendarConnectionSave(crawlingResult);
        } catch (Exception e) {
            log.info(e.toString());
            new ResponseEntity<>("오류가 발생했습니다!", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        //날짜별 클릭수 저장하기
        clickRateCount.clickRateCount(userId, LocalDate.now());

        Map<String, Object> response = new HashMap<>();
        response.put("contents", crawlingResult);

        //필요한 것- 저장할 Entity, 오늘의 날짜 정보, 저장할 Entity의 Repository
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    public void calendarConnectionSave(ArrayList<CalendarInfo> crawlingData) throws Exception {

        for (CalendarInfo cal : crawlingData) {
            String number = cal.getReservationNumber();
            log.info(cal.toString() + " \n");
            CalendarInfo foundCal = calendarInfoRepository.findByReservationNumber(number);

            if (foundCal == null) {
                calendarInfoRepository.save(cal);
            } else {
                foundCal.setStartTime(cal.getStartTime());
                foundCal.setEndTime(cal.getEndTime());
                foundCal.setPrice(cal.getPrice());
                foundCal.setLocation(cal.getLocation());
                foundCal.setPlatform(cal.getPlatform());
                foundCal.setProcess(cal.getProcess());
                foundCal.setCustomer(cal.getCustomer());
                foundCal.setReservationNumber(cal.getReservationNumber());
                foundCal.setLink(cal.getLink());

                calendarInfoRepository.save(foundCal);
            }
        }
    }

    @Transactional
    public ResponseEntity<?> getCalendarInfoByMonth(HttpServletRequest request, int month, int year) {
        String accessToken = jwtTokenService.getAccessToken(request);
        String userId = jwtTokenService.getUserIdFromToken(accessToken);

        // 월별 조회를 위한 날짜 범위 계산
        LocalDate startDate = LocalDate.of(year, month, 1); // 해당 월의 첫 날
        LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth()); // 해당 월의 마지막 날

        // 해당 기간 내의 데이터 조회
        List<CalendarInfo> calendarInfos = calendarInfoRepository
                .findAllByUserIdAndStartTimeBetween(userId, startDate.atStartOfDay(), endDate.atTime(23, 59, 59));

        if (calendarInfos.isEmpty()) {
            return new ResponseEntity<>("조회된 데이터가 없습니다.", HttpStatus.NOT_FOUND);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("userId", userId);
        response.put("month", month);
        response.put("year", year);
        response.put("data", calendarInfos);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }


}

