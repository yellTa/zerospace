package com.zerospace.zerospace.service.utils;

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
            return null;
        }
    }

}
