package it.univr.track.e2e;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class DeviceListPage extends BasePage{
    public DeviceListPage(WebDriver driver) {
        super(driver);
    }

    public String decommissionDevice(String uuid) {
        driver.get("http://localhost:8080/web/devices");

        String xpath = String.format("//tr[contains(., '%s')]//button[@title='Dismetti']", uuid);

        wait.until(ExpectedConditions.elementToBeClickable(By.xpath(xpath))).click();

        wait.until(ExpectedConditions.alertIsPresent());
        driver.switchTo().alert().accept();

        wait.until(ExpectedConditions.urlContains("/web/devices"));

        String rowXpath = String.format("//tr[contains(., '%s')]", uuid);
        wait.until(ExpectedConditions.textToBePresentInElementLocated(By.xpath(rowXpath), "DISMESSO"));

        return rowXpath;

    }

    public void await(String rowXpath){

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(rowXpath)));


    }
}
