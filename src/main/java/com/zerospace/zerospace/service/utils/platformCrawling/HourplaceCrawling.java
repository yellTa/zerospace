package com.zerospace.zerospace.service.utils.platformCrawling;

import com.zerospace.zerospace.domain.CalendarInfo;
import com.zerospace.zerospace.exception.CrawlingException;
import com.zerospace.zerospace.exception.LoginFailedException;
import io.github.bonigarcia.wdm.WebDriverManager;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
@Slf4j
public class HourplaceCrawling {
    public WebDriver hourplaceLogin(String id, String password) {

        try {
            WebDriverManager.chromedriver().setup();
            ChromeOptions options = new ChromeOptions();
            options.setExperimentalOption("excludeSwitches", new String[]{"enable-automation"});
            options.addArguments("--headless=new");
            options.addArguments("--single-process");
            options.addArguments("--no-sandbox");
            options.addArguments("--disable-dev-shm-usage");

            //암시적 대기, size설정
            WebDriver driver = new ChromeDriver(options);
            driver.manage().window().setSize(new Dimension(1024, 4000));

            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(120));

            //hourplace 사이트 가져트
            driver.get("https://hourplace.co.kr/");

            WebElement loginAndProfileBtn = driver.findElement(By.xpath("//*[@id=\"header\"]/div/div[2]/div[4]/p"));
            loginAndProfileBtn.click();

            WebElement loginEmail = driver.findElement(By.xpath("//*[@id=\"email\"]"));
            loginEmail.sendKeys(id);

            WebElement loginPassword = driver.findElement(By.xpath("//*[@id=\"password\"]"));
            loginPassword.sendKeys(password);

            WebElement enterLoginBtn = driver.findElement(By.xpath("//*[@id=\"login_vue\"]/div/div[3]/div[4]"));
            enterLoginBtn.click();

            Thread.sleep(500);
            WebElement check = driver.findElement(By.xpath("//*[@id=\"login_vue\"]/div/div[3]/div[3]/p"));
            String checkMessage = check.getText();

            if (checkMessage.contains("이메일 및 비밀번호를 확인해 주세요.")) {
                driver.close();
                driver.quit();
                return null;
            }
            return driver;
        } catch (Exception e) {
            log.info(e.toString());
            throw new LoginFailedException("알 수 없는 에러가 발생했습니다.");
        }
    }

    public ArrayList<CalendarInfo> hourspaceGetInfo(WebDriver driver) {
        ArrayList<CalendarInfo> result = new ArrayList<>();

        try {
            //Profile
            WebElement menu = driver.findElement(By.xpath("//*[@id=\"header\"]/div/div[2]/div[4]/div/img"));
            menu.click();

            //예약 내역 클릭
            WebElement reservationCheckBtn = driver.findElement(By.xpath("//*[@id=\"header\"]/div/div[4]/div[5]/div/p"));
            reservationCheckBtn.click();

            log.info("reservation list get start");
            List<WebElement> list = driver.findElements(By.cssSelector("div.py-\\[30px\\]"));


            for (int i = 0; i < Math.min(list.size(), 20); i++) {
                log.info("contents size = {}", list.size());
                log.info("---------------------");
                CalendarInfo calendarInfo = new CalendarInfo();
                calendarInfo.setPlatform("hourplace");
                WebElement div = list.get(i);

                //시간
                WebElement timeAndPlace = div.findElement(By.cssSelector("div.w-\\[429px\\].text-default.font-normal.text-gray080"));
                WebElement time = timeAndPlace.findElement(By.cssSelector("p.w-full.text-ellipsis.whitespace-pre-wrap.break-words.text-default.font-normal.text-gray080.line-clamp-1"));
                log.info(time.getText());
                calendarInfo = divideDateData(time.getText(), calendarInfo);


                WebElement location = timeAndPlace.findElement(By.cssSelector("p.mb-2.w-full.text-ellipsis.whitespace-pre-wrap.break-words.text-default.font-normal.text-gray080.line-clamp-1"));
                log.info("location ={}", location.getText());
                calendarInfo.setLocation(location.getText());

                WebElement price = div.findElement(By.cssSelector("p.leading-\\[24px\\]"));
                log.info("price = {}", price.getText());
                calendarInfo.setPrice(price.getText());

                WebElement process = div.findElement(By.cssSelector("p.text-default.font-bold"));
                log.info("process = {}", process.getText());
                calendarInfo.setProcess(process.getText());

                WebElement reservationNumber = div.findElement(By.cssSelector("span.text-default.font-normal.text-gray080"));
                log.info("reservationNumber = {}", reservationNumber.getText());
                calendarInfo.setReservationNumber(reservationNumber.getText());

                WebElement customer = div.findElement(By.cssSelector("p.ml-1.text-ellipsis.whitespace-pre-line.break-words.text-default.font-normal.text-gray080.line-clamp-1"));
                log.info("customer = {}", customer.getText());
                calendarInfo.setCustomer(customer.getText());

                String bookinglink = "https://hourplace.co.kr/booking/" + reservationNumber.getText();
                log.info("booking link = {}", bookinglink);
                calendarInfo.setLink(bookinglink);

                result.add(calendarInfo);
            }
            driver.close();
            driver.quit();

        } catch (Exception e) {
            log.info(e.toString());
            driver.close();
            driver.quit();
            throw new CrawlingException("알 수 없는 에러가 발생했습니다. 다시 시도해주세요");
        }
        return result;
    }

    public CalendarInfo divideDateData(String text, CalendarInfo calendarInfo) {
        Pattern singleDatePattern = Pattern.compile("(\\d{4})년 (\\d{1,2})월 (\\d{1,2})일(?:\\(.*?\\))?\\s*(오전|오후)\\s*(\\d{1,2}:\\d{2}) ~ (오전|오후)\\s*(\\d{1,2}:\\d{2})");

        Pattern twoDatePattern = Pattern.compile(
                "(\\d{4})년 (\\d{1,2})월 (\\d{1,2})일(?:\\(.*?\\))?\\s*(오전|오후)\\s*(\\d{1,2}:\\d{2})\\s*~\\s*(\\d{1,2})월 (\\d{1,2})일(?:\\(.*?\\))?\\s*(오전|오후)\\s*(\\d{1,2}:\\d{2})");

        Matcher singleDateMatcher = singleDatePattern.matcher(text);
        Matcher twoDateMatcher = twoDatePattern.matcher(text);

        LocalDateTime startDateTime = null;
        LocalDateTime endDateTime = null;

        if (singleDateMatcher.find()) {
            // Single date pattern
            int year = Integer.parseInt(singleDateMatcher.group(1));
            int month = Integer.parseInt(singleDateMatcher.group(2));
            int day = Integer.parseInt(singleDateMatcher.group(3));
            String startPeriod = singleDateMatcher.group(4);
            String startTimeStr = singleDateMatcher.group(5);
            String endPeriod = singleDateMatcher.group(6);
            String endTimeStr = singleDateMatcher.group(7);

            LocalTime startTime = parseTime(startPeriod, startTimeStr);
            LocalTime endTime = parseTime(endPeriod, endTimeStr);

            LocalDate date = LocalDate.of(year, month, day);

            // LocalDateTime으로 변경
            startDateTime = LocalDateTime.of(date, startTime);
            endDateTime = LocalDateTime.of(date, endTime);

            log.info("Start DateTime: " + startDateTime);
            log.info("End DateTime: " + endDateTime);

        } else if (twoDateMatcher.find()) {
            // Two date pattern
            int startYear = Integer.parseInt(twoDateMatcher.group(1));
            int startMonth = Integer.parseInt(twoDateMatcher.group(2));
            int startDay = Integer.parseInt(twoDateMatcher.group(3));
            String startPeriod = twoDateMatcher.group(4);
            String startTimeStr = twoDateMatcher.group(5);

            int endMonth = Integer.parseInt(twoDateMatcher.group(6));
            int endDay = Integer.parseInt(twoDateMatcher.group(7));
            String endPeriod = twoDateMatcher.group(8);
            String endTimeStr = twoDateMatcher.group(9);

            LocalTime startTime = parseTime(startPeriod, startTimeStr);
            LocalTime endTime = parseTime(endPeriod, endTimeStr);

            int endYear = startYear;
            if (endMonth < startMonth || (endMonth == startMonth && endDay < startDay)) {
                endYear += 1;
            }

            LocalDate startDate = LocalDate.of(startYear, startMonth, startDay);
            LocalDate endDate = LocalDate.of(endYear, endMonth, endDay);

            // LocalDateTime으로 변경
            startDateTime = LocalDateTime.of(startDate, startTime);
            endDateTime = LocalDateTime.of(endDate, endTime);

            log.info("Start DateTime: " + startDateTime);
            log.info("End DateTime: " + endDateTime);

        } else {
            log.error("Pattern did not match");
        }

        // CalendarInfo에 LocalDateTime 저장
        calendarInfo.setStartTime(startDateTime);
        calendarInfo.setEndTime(endDateTime);

        return calendarInfo;
    }

    private static LocalTime parseTime(String period, String time) {
        LocalTime parsedTime = LocalTime.parse(time);

        // If it's PM and not 12:00 PM, add 12 hours to convert to 24-hour format
        if (period.equals("오후") && !parsedTime.equals(LocalTime.NOON)) {
            parsedTime = parsedTime.plusHours(12);
        }
        return parsedTime;
    }


}
