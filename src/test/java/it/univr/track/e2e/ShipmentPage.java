package it.univr.track.e2e;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class ShipmentPage extends BasePage{

    public ShipmentPage(WebDriver driver) { super(driver); }

    public void createShipment(String id, String dest, String desc) {
        driver.get("http://localhost:8080/web/newShipment");

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("shipmentId"))).sendKeys(id);
        driver.findElement(By.name("destination")).sendKeys(dest);
        driver.findElement(By.name("description")).sendKeys(desc);

        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[contains(., 'REGISTRA')]"))).click();

        wait.until(ExpectedConditions.urlContains("/web/shipments"));

    }

    public void completeShipment(String shipmentId) {
        driver.get("http://localhost:8080/web/shipments");

        String xpath = String.format("//tr[contains(., '%s')]//button[contains(., 'COMPLETA')]", shipmentId);

        wait.until(ExpectedConditions.elementToBeClickable(By.xpath(xpath))).click();

        wait.until(ExpectedConditions.alertIsPresent());
        driver.switchTo().alert().accept();

        wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//span[contains(text(), 'ARCHIVIATA')]")));
    }
}
