package it.univr.track.e2e;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT) // AVVIA IL SERVER
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class SmartTrackingE2ETest {

    private static WebDriver driver;
    private static ProvisionPage provisionPage;
    private static ShipmentPage shipmentPage;

    @BeforeAll
    static void setup() {
        WebDriverManager.chromedriver().create();
        ChromeOptions options = new ChromeOptions();
        driver = new ChromeDriver(options);

        provisionPage = new ProvisionPage(driver);
        shipmentPage = new ShipmentPage(driver);
    }

    @Test
    @DisplayName("UC1 - Registrazione nuovo utente")
    void testUserRegistration() {
        SignUpPage signUpPage = new SignUpPage(driver);

        // Generiamo uno username unico per evitare conflitti nel database H2
        String uniqueUser = "user_" + System.currentTimeMillis();

        // Esecuzione registrazione
        signUpPage.register(uniqueUser, "password123", "USER");

        // Verifica la presenza del messaggio di successo nel template
        assertTrue(driver.getCurrentUrl().contains("success"));

        // Test di login con le nuove credenziali per confermare la persistenza
        provisionPage.login(uniqueUser, "password123");

        // Verifica l'accesso al profilo
        assertTrue(driver.getPageSource().contains(uniqueUser));
    }

    @Test
    @DisplayName("UC1 - Provisioning di un nuovo dispositivo")
    void testProvisioning() {
        provisionPage.login("admin", "123456789");
        provisionPage.registerDevice("SN-2026-TEST");

        String currentUrl = driver.getCurrentUrl();
        assertTrue(currentUrl.contains("/web/devices"));
        assertTrue(driver.getPageSource().contains("SN-2026-TEST"));
    }

    @Test
    @DisplayName("UC3 - Creazione Nuova Spedizione")
    void testCreateShipment() {
        provisionPage.login("admin", "123456789");

        shipmentPage.createShipment("SH-TEST-002", "Verona", "Beni sensibili test");

        assertTrue(driver.getCurrentUrl().contains("/web/shipments"));
        assertTrue(driver.getPageSource().contains("SH-TEST-002"));
    }

    @Test
    @DisplayName("UC4 - Associazione Device a Spedizione")
    void testAssociateDevice() {
        provisionPage.login("admin", "123456789");
        provisionPage.registerDevice("SN-2027-TEST");
        shipmentPage.createShipment("SH-TEST-003", "Verona", "Beni sensibili test");


        driver.get("http://localhost:8080/web/shipments");
        // Clicca sull'icona link per l'allocazione
        driver.findElement(By.cssSelector("a[title='Associa Sensore']")).click();

        // Seleziona il primo device disponibile nel dropdown
        driver.findElement(By.name("deviceUid")).click();
        driver.findElement(By.xpath("//option[contains(text(), 'SN-2027-TEST')]")).click();

        driver.findElement(By.xpath("//button[contains(., 'CONFERMA ALLOCAZIONE')]")).click();

        // Verifica che il contatore device sia aggiornato
        assertTrue(driver.getPageSource().contains("SH-TEST-003"));
        // Verifica lo stato IN USO (ACTIVE) nella tabella devices
        driver.get("http://localhost:8080/web/devices");
        assertTrue(driver.getPageSource().contains("IN USO"));
    }

    @Test
    @DisplayName("UC6 - Verifica visibilità spedizione su mappa dopo associazione")
    void testShipmentVisibilityOnMap() {
        MapPage mapPage = new MapPage(driver);

        provisionPage.login("admin", "123456789");
        provisionPage.registerDevice("DEV-MAP");
        shipmentPage.createShipment("MAP-TEST", "Verona", "Beni sensibili test");


        driver.get("http://localhost:8080/web/shipments");
        // Clicca sull'icona link per l'allocazione
        driver.findElement(By.cssSelector("a[title='Associa Sensore']")).click();

        // Seleziona il primo device disponibile nel dropdown
        driver.findElement(By.name("deviceUid")).click();
        driver.findElement(By.xpath("//option[contains(text(), 'DEV-MAP')]")).click();

        driver.findElement(By.xpath("//button[contains(., 'CONFERMA ALLOCAZIONE')]")).click();

        // Verifica che il contatore device sia aggiornato
        assertTrue(driver.getPageSource().contains("MAP-TEST"));
        // Verifica lo stato IN USO (ACTIVE) nella tabella devices
        driver.get("http://localhost:8080/web/devices");
        assertTrue(driver.getPageSource().contains("IN USO"));

        mapPage.navigateToMap();

        // Verifica che la spedizione appaia nella lista a sinistra
        assertTrue(mapPage.isShipmentInSidebar("MAP-TEST"),
                "La spedizione " + "MAP-TEST" + " dovrebbe essere visibile nella sidebar della mappa.");

        // 3. Click sulla spedizione per attivare il focus della mappa
        driver.findElement(By.cssSelector("div[data-id='" + "MAP-TEST" + "']")).click();

        assertTrue(driver.findElement(By.id("map")).isDisplayed());
    }



    @Test
    @DisplayName("UC8 - Decommissioning di un dispositivo")
    void testDecommissionDevice() {
        provisionPage.login("admin", "123456789");

        DeviceListPage deviceListPage = new DeviceListPage(driver);

        // 1. Setup: Registriamo un device univoco da dismettere
        String devToKill = "DEV-TO-KILL-" + System.currentTimeMillis();
        provisionPage.registerDevice(devToKill);

        // 2. Esecuzione: Dismettiamo il dispositivo
        deviceListPage.decommissionDevice(devToKill);



        // Cerchiamo la riga del device e verifichiamo che contenga l'etichetta DISMESSO
        String rowXpath = String.format("//tr[contains(., '%s')]", devToKill);
        deviceListPage.await(rowXpath);
        String rowText = driver.findElement(By.xpath(rowXpath)).getText();
        assertTrue(rowText.contains("DISMESSO"));
    }

    @Test
    @DisplayName("UC9 - Completamento Spedizione")
    void testCompleteShipment() {
        provisionPage.login("admin", "123456789");
        provisionPage.registerDevice("SN-2028-TEST");
        shipmentPage.createShipment("SH-TEST-005", "Verona", "Beni sensibili test");
        shipmentPage.completeShipment("SH-TEST-005");

        // Verifica che lo stato sia diventato ARCHIVIATA
        assertTrue(driver.getPageSource().contains("ARCHIVIATA"));
    }

    @AfterAll
    static void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}