package it.univr.track.e2e;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class SignUpPage extends BasePage{

    public SignUpPage(WebDriver driver) {
        super(driver);
    }

    public void register(String username, String password, String role) {
        driver.get("http://localhost:8080/user/signup");

        // Attesa esplicita per evitare i blocchi di caricamento
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("username"))).sendKeys(username);
        driver.findElement(By.name("password")).sendKeys(password);
        driver.findElement(By.name("confirmPassword")).sendKeys(password);

        // Selezione del ruolo dal menu a tendina
        driver.findElement(By.name("role")).sendKeys(role);

        // Invio del form
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[contains(., 'REGISTRA ACCOUNT')]"))).click();

        wait.until(ExpectedConditions.urlContains("/user/signin"));

    }
}
