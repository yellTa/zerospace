package com.zerospace.zerospace.service.utils;

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

@Component
@RequiredArgsConstructor
@Slf4j
public class SpacecloudCrawling {

    public WebDriver spacecloudLogin(String id, String password) {
        WebDriver driver=null;
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
        }

        return driver;
    }

}
