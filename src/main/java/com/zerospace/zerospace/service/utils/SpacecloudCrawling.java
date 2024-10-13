package com.zerospace.zerospace.service.utils;

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
import java.time.LocalTime;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
@RequiredArgsConstructor
@Slf4j
public class SpacecloudCrawling {

    public WebDriver spacecloudLogin(String id, String password) {
        WebDriver driver = null;
        try {
            WebDriverManager.chromedriver().setup();

            ChromeOptions options = new ChromeOptions();
            options.setExperimentalOption("excludeSwitches", new String[]{"enable-automation"});

            //암시적 대기, size설정
            driver = new ChromeDriver(options);
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


        } catch (Exception e) {
            log.info(e.toString());
            throw new LoginFailedException("알 수 없는 에러가 발생했습니다.");
        }

        return driver;
    }

    public void spacecloudGetInfo(WebDriver driver) {
        try {
            //예약 정보 가져오기
            List<WebElement> list = driver.findElements(By.cssSelector("article.list_box"));
            log.info("size olf list  = {}", list.size());

            //hourplace 20개 spacecloud : 10개
            for (int i = 0; i < Math.min(list.size(), 10); i++) {
                log.info("----------------");
                WebElement div = list.get(i);

                WebElement place = div.findElement(By.cssSelector("dd.place"));
                log.info("place  = {}", place.getText());

                WebElement time = div.findElement(By.cssSelector("dd.date"));
                log.info("time = {}", time.getText());
                divideDateData(time.getText());

                WebElement price = div.findElement(By.cssSelector("p.price"));
                log.info("price = {}", price.getText());

                WebElement process = div.findElement(By.cssSelector("span.tag"));
                log.info("process = {}", process.getText());

                WebElement reservationNumber = div.findElement(By.cssSelector("span.reservation_num"));
                log.info(reservationNumber.getText());

                WebElement customer = div.findElement(By.cssSelector("dd.sub_detail"));
                log.info("customer= {}", customer.getText().split("\n")[0]);

                String bookinglink = "https://partner.spacecloud.kr/reservation/" + reservationNumber.getText();
                log.info("spaceBooking = {}", bookinglink);

            }

            driver.close();
            driver.quit();
        } catch (Exception e) {
            log.info(e.toString());
            driver.close();
            driver.quit();
            throw new CrawlingException("알 수 없는 에러가 발생했습니다. 다시 시도해주세요");
        }
    }

    private void divideDateData(String text) {

        // Extracting year, month, day using regex
        Pattern datePattern = Pattern.compile("(\\d{4})\\.(\\d{2})\\.(\\d{2})");
        Matcher dateMatcher = datePattern.matcher(text);

        int year = 0, month = 0, day = 0;
        if (dateMatcher.find()) {
            year = Integer.parseInt(dateMatcher.group(1));
            month = Integer.parseInt(dateMatcher.group(2));
            day = Integer.parseInt(dateMatcher.group(3));
        }

        // Extracting start and end times using regex
        Pattern timePattern = Pattern.compile("(\\d{1,2})~(\\d{1,2}) 시");
        Matcher timeMatcher = timePattern.matcher(text);

        LocalTime startTime = null;
        LocalTime endTime = null;
        if (timeMatcher.find()) {
            int startHour = Integer.parseInt(timeMatcher.group(1));
            int endHour = Integer.parseInt(timeMatcher.group(2));

            // Convert hours to LocalTime
            startTime = LocalTime.of(startHour, 0);
            endTime = LocalTime.of(endHour, 0);
        }

        log.info("year : " + year);
        log.info("month : " + month);
        log.info("days : " + day);
        log.info("startTime : " + startTime);
        log.info("endTime : " + endTime);
    }
}
