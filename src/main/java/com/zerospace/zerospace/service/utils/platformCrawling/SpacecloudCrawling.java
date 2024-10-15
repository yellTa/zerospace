package com.zerospace.zerospace.service.utils.platformCrawling;

import com.zerospace.zerospace.domain.CalendarInfo;
import com.zerospace.zerospace.exception.CrawlingException;
import com.zerospace.zerospace.exception.LoginFailedException;
import io.github.bonigarcia.wdm.WebDriverManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
@RequiredArgsConstructor
@Slf4j
public class SpacecloudCrawling {

    public WebDriver spacecloudLogin(String id, String password) {

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
            driver.get("https://partner.spacecloud.kr/");

            WebElement loginBtn = driver.findElement(By.xpath("//*[@id=\"_login\"]"));
            loginBtn.click();

            WebElement loginEmail = driver.findElement(By.xpath("//*[@id=\"email\"]"));
            loginEmail.sendKeys(id);

            WebElement loginPassword = driver.findElement(By.xpath("//*[@id=\"pw\"]"));
            loginPassword.sendKeys(password);

            WebElement enterLoginBtn = driver.findElement(By.xpath("//*[@id=\"content_wraper\"]/section/div/form/fieldset/button"));
            enterLoginBtn.click();

            //page 넘어갈때 까지 대기하기
            String currentUrl = driver.getCurrentUrl();

            try {
                WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
                wait.until(ExpectedConditions.not(ExpectedConditions.urlToBe(currentUrl)));
            } catch (Exception e) {
                driver.close();
                driver.quit();

                return null;
            }

            log.info("url Changed!");
            WebElement menu = driver.findElement(By.xpath("//*[@id=\"root\"]/div[1]/header/div/a/span"));
            menu.click();

            //네비게이션 바 열릴 때 까지 기다리기
            Thread.sleep(1000);

            WebElement reservationCheckBtn = driver.findElement(By.xpath("//*[@id=\"all_menu\"]/div/div/div/ul/li[1]/a/span[1]"));
            reservationCheckBtn.click();

            return driver;
        } catch (Exception e) {
            log.info(e.toString());
            throw new LoginFailedException("알 수 없는 에러가 발생했습니다.");
        }


    }

    public ArrayList<CalendarInfo> spacecloudGetInfo(WebDriver driver) {
        ArrayList<CalendarInfo> result = new ArrayList<>();

        try {
            //예약 정보 가져오기
            List<WebElement> list = driver.findElements(By.cssSelector("article.list_box"));
            log.info("size olf list  = {}", list.size());

            //hourplace 20개 spacecloud : 10개
            for (int i = 0; i < Math.min(list.size(), 10); i++) {
                log.info("----------------");
                WebElement div = list.get(i);

                CalendarInfo calendarInfo = new CalendarInfo();
                calendarInfo.setPlatform("spacecloud");

                WebElement location = div.findElement(By.cssSelector("dd.place"));
                log.info("location  = {}", location.getText());
                calendarInfo.setLocation(location.getText());

                WebElement time = div.findElement(By.cssSelector("dd.date"));
                log.info("time = {}", time.getText());
                calendarInfo = divideDateData(time.getText(), calendarInfo);

                WebElement price = div.findElement(By.cssSelector("p.price"));
                log.info("price = {}", price.getText());
                calendarInfo.setPrice(price.getText());

                WebElement process = div.findElement(By.cssSelector("span.tag"));
                log.info("process = {}", process.getText());
                calendarInfo.setProcess(process.getText());

                WebElement reservationNumber = div.findElement(By.cssSelector("span.reservation_num"));
                log.info(reservationNumber.getText());
                calendarInfo.setRevervationNumber(reservationNumber.getText().split(" ")[1]);

                WebElement customer = div.findElement(By.cssSelector("dd.sub_detail"));
                log.info("customer= {}", customer.getText().split("\n")[0]);
                calendarInfo.setCustomer(customer.getText().split("\n")[0]);

                String bookinglink = "https://partner.spacecloud.kr/reservation/" + reservationNumber.getText().split(" ")[1];
                log.info("spaceBooking = {}", bookinglink);
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
        // 첫 번째 패턴: 2024.10.31 (목) 23시 ~ 2024.11.01 (금) 1시, 2시간
        Pattern firstPattern = Pattern.compile("(\\d{4})\\.(\\d{1,2})\\.(\\d{1,2}) \\(.*?\\) (\\d{1,2})시 ~ (\\d{4})\\.(\\d{1,2})\\.(\\d{1,2}) \\(.*?\\) (\\d{1,2})시");
        // 두 번째 패턴: 2024.10.12(토) 18~19 시, 1 시간
        Pattern secondPattern = Pattern.compile("(\\d{4})\\.(\\d{1,2})\\.(\\d{1,2})\\(.*?\\) (\\d{1,2})~(\\d{1,2}) 시");

        Matcher firstMatcher = firstPattern.matcher(text);
        Matcher secondMatcher = secondPattern.matcher(text);

        LocalDateTime startDateTime = null;
        LocalDateTime endDateTime = null;

        if (firstMatcher.find()) {
            int startYear = Integer.parseInt(firstMatcher.group(1));
            int startMonth = Integer.parseInt(firstMatcher.group(2));
            int startDay = Integer.parseInt(firstMatcher.group(3));
            int startHour = Integer.parseInt(firstMatcher.group(4));

            int endYear = Integer.parseInt(firstMatcher.group(5));
            int endMonth = Integer.parseInt(firstMatcher.group(6));
            int endDay = Integer.parseInt(firstMatcher.group(7));
            int endHour = Integer.parseInt(firstMatcher.group(8));

            LocalDate startDate = LocalDate.of(startYear, startMonth, startDay);
            LocalDate endDate = LocalDate.of(endYear, endMonth, endDay);

            LocalTime startTime = LocalTime.of(startHour, 0);
            LocalTime endTime = LocalTime.of(endHour, 0);

            startDateTime = LocalDateTime.of(startDate, startTime);
            endDateTime = LocalDateTime.of(endDate, endTime);

            log.info("Start DateTime: " + startDateTime);
            log.info("End DateTime: " + endDateTime);

        } else if (secondMatcher.find()) {
            int year = Integer.parseInt(secondMatcher.group(1));
            int month = Integer.parseInt(secondMatcher.group(2));
            int day = Integer.parseInt(secondMatcher.group(3));
            int startHour = Integer.parseInt(secondMatcher.group(4));
            int endHour = Integer.parseInt(secondMatcher.group(5));

            LocalDate date = LocalDate.of(year, month, day);

            LocalTime startTime = LocalTime.of(startHour, 0);
            LocalTime endTime = LocalTime.of(endHour, 0);

            startDateTime = LocalDateTime.of(date, startTime);
            endDateTime = LocalDateTime.of(date, endTime);

            log.info("Start DateTime: " + startDateTime);
            log.info("End DateTime: " + endDateTime);

        } else {
            log.info("Pattern did not match");
        }

        // CalendarInfo에 LocalDateTime 저장
        calendarInfo.setStartTime(startDateTime);
        calendarInfo.setEndTime(endDateTime);

        return calendarInfo;
    }

}
