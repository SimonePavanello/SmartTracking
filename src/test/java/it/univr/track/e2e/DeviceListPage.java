package it.univr.track.e2e;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class DeviceListPage extends BasePage{
    public DeviceListPage(WebDriver driver) {
        super(driver);
    }

    public void decommissionDevice(String uuid) {
        driver.get("http://localhost:8080/web/devices");

        // XPath dinamico per trovare il tasto "Dismetti" (icona trash) nella riga del device specifico
        String xpath = String.format("//tr[contains(., '%s')]//button[@title='Dismetti']", uuid);

        // Aspetta che il tasto sia cliccabile e clicca
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath(xpath))).click();

        // Gestione del popup di conferma nativo del browser (confirm JS)
        wait.until(ExpectedConditions.alertIsPresent());
        driver.switchTo().alert().accept();

        wait.until(ExpectedConditions.urlContains("/web/devices"));

    }

    public void await(String rowXpath){

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(rowXpath)));


    }
}
