package com.zerospace.zerospace.service.utils;

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
import java.time.LocalTime;
import java.util.List;
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

    public void hourspaceGetInfo(WebDriver driver) {
        try {
            //Profile
            WebElement menu = driver.findElement(By.xpath("//*[@id=\"header\"]/div/div[2]/div[4]/div/img"));
            menu.click();

            //예약 내역 클릭
            WebElement reservationCheckBtn = driver.findElement(By.xpath("//*[@id=\"header\"]/div/div[4]/div[5]/div/p"));
            reservationCheckBtn.click();

            log.info("list get start");
            //에약 정보 가져오기
            List<WebElement> list = driver.findElements(By.cssSelector("div.py-\\[30px\\]"));

            // 최신 5개 혹은 그 이하면 이하의 수 만큼
            for (int i = 0; i < Math.min(list.size(), 20); i++) {
                log.info("contents size = {}", list.size());
                log.info("---------------------");

                WebElement div = list.get(i);

                //시간
                WebElement timeAndPlace = div.findElement(By.cssSelector("div.w-\\[429px\\].text-default.font-normal.text-gray080"));
                WebElement time = timeAndPlace.findElement(By.cssSelector("p.w-full.text-ellipsis.whitespace-pre-wrap.break-words.text-default.font-normal.text-gray080.line-clamp-1"));
                divideDateData(time.getText());

                //장소이름
                WebElement place = timeAndPlace.findElement(By.cssSelector("p.mb-2.w-full.text-ellipsis.whitespace-pre-wrap.break-words.text-default.font-normal.text-gray080.line-clamp-1"));

                log.info("place ={}", place.getText());

                //금액
                WebElement price = div.findElement(By.cssSelector("p.leading-\\[24px\\]"));
                log.info("price = {}", price.getText());

                //진행상황
                WebElement process = div.findElement(By.cssSelector("p.text-default.font-bold"));
                log.info("process = {}", process.getText());

                //예약현황
                WebElement reservationNumber = div.findElement(By.cssSelector("span.text-default.font-normal.text-gray080"));
                log.info("reservationNumber = {}", reservationNumber.getText());
            }
            //끝나면 driver  닫기

            driver.close();
            driver.quit();

        } catch (Exception e) {
            driver.close();
            driver.quit();
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
        Pattern timePattern = Pattern.compile("(\\d{2})~(\\d{2}) 시");
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
