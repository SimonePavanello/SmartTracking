package it.univr.track.e2e;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class ProvisionPage extends BasePage{

    public ProvisionPage(WebDriver driver) { super(driver); }

    public void registerDevice(String uid) {
        driver.get("http://localhost:8080/web/provision");
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("uid"))).sendKeys(uid);
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[contains(., 'COMPLETA')]"))).click();
        wait.until(ExpectedConditions.urlContains("/web/devices"));
    }
}
