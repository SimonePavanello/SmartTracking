package it.univr.track.e2e;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.List;

public class MapPage extends BasePage{
    public MapPage(WebDriver driver) {
        super(driver);
    }

    public void navigateToMap() {
        driver.get("http://localhost:8080/web/map");
    }

    public boolean isShipmentInSidebar(String shipmentId) {
        // Aspetta che gli elementi della sidebar siano caricati
        wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("aside div.space-y-3")));

        // Cerca l'elemento specifico tramite l'attributo data-id
        List<WebElement> shipments = driver.findElements(By.cssSelector("div[data-id='" + shipmentId + "']"));
        return !shipments.isEmpty();
    }
}
