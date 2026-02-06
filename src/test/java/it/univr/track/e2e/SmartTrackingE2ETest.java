package it.univr.track.e2e;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT) // AVVIA IL SERVER
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class SmartTrackingE2ETest {

    private static WebDriver driver;
    private static ProvisionPage provisionPage;
    private static ShipmentPage shipmentPage;

    @BeforeAll
    static void setup() {
        WebDriverManager.firefoxdriver().create();
        FirefoxOptions options = new FirefoxOptions();
        driver = new FirefoxDriver(options);

        provisionPage = new ProvisionPage(driver);
        shipmentPage = new ShipmentPage(driver);
    }

    @Test
    @DisplayName("UC1 - Register New User")
    void testUserRegistration() {
        SignUpPage signUpPage = new SignUpPage(driver);

        String uniqueUser = "user_" + System.currentTimeMillis();

        signUpPage.register(uniqueUser, "password123", "USER");

        assertTrue(driver.getCurrentUrl().contains("success"));

        provisionPage.login(uniqueUser, "password123");

        assertTrue(driver.getPageSource().contains(uniqueUser));
    }

    @Test
    @DisplayName("UC1 - Provisioning New Device")
    void testProvisioning() {
        provisionPage.login("admin", "123456789");
        provisionPage.registerDevice("SN-2026-TEST");

        String currentUrl = driver.getCurrentUrl();
        assertTrue(currentUrl.contains("/web/devices"));
        assertTrue(driver.getPageSource().contains("SN-2026-TEST"));
    }

    @Test
    @DisplayName("UC3 - Create New Shipment")
    void testCreateShipment() {
        provisionPage.login("admin", "123456789");

        shipmentPage.createShipment("SH-TEST-002", "Verona", "Beni sensibili test");

        assertTrue(driver.getCurrentUrl().contains("/web/shipments"));
        assertTrue(driver.getPageSource().contains("SH-TEST-002"));
    }

    @Test
    @DisplayName("UC4 - Associate Device to Shipment")
    void testAssociateDevice() {
        provisionPage.login("admin", "123456789");
        provisionPage.registerDevice("SN-2027-TEST");
        shipmentPage.createShipment("SH-TEST-003", "Verona", "Beni sensibili test");


        driver.get("http://localhost:8080/web/shipments");
        driver.findElement(By.cssSelector("a[title='Associa Sensore']")).click();

        driver.findElement(By.name("deviceUid")).click();
        driver.findElement(By.xpath("//option[contains(text(), 'SN-2027-TEST')]")).click();

        driver.findElement(By.xpath("//button[contains(., 'CONFERMA ALLOCAZIONE')]")).click();

        assertTrue(driver.getPageSource().contains("SH-TEST-003"));
        driver.get("http://localhost:8080/web/devices");
        assertTrue(driver.getPageSource().contains("IN USO"));
    }

    @Test
    @DisplayName("UC6 - Verify Shipment Visibility on Map After Association")
    void testShipmentVisibilityOnMap() {
        MapPage mapPage = new MapPage(driver);

        provisionPage.login("admin", "123456789");
        provisionPage.registerDevice("DEV-MAP");
        shipmentPage.createShipment("MAP-TEST", "Verona", "Beni sensibili test");


        driver.get("http://localhost:8080/web/shipments");
        driver.findElement(By.cssSelector("a[title='Associa Sensore']")).click();

        driver.findElement(By.name("deviceUid")).click();
        driver.findElement(By.xpath("//option[contains(text(), 'DEV-MAP')]")).click();

        driver.findElement(By.xpath("//button[contains(., 'CONFERMA ALLOCAZIONE')]")).click();

        assertTrue(driver.getPageSource().contains("MAP-TEST"));
        driver.get("http://localhost:8080/web/devices");
        assertTrue(driver.getPageSource().contains("IN USO"));

        mapPage.navigateToMap();

        assertTrue(mapPage.isShipmentInSidebar("MAP-TEST"),
                "La spedizione " + "MAP-TEST" + " dovrebbe essere visibile nella sidebar della mappa.");

        driver.findElement(By.cssSelector("div[data-id='" + "MAP-TEST" + "']")).click();

        assertTrue(driver.findElement(By.id("map")).isDisplayed());
    }


    @Test
    @DisplayName("UC8 - Device Decommissioning")
    void testDecommissionDevice() {
        provisionPage.login("admin", "123456789");

        DeviceListPage deviceListPage = new DeviceListPage(driver);

        String devToKill = "DEV-TO-KILL-" + System.currentTimeMillis();
        provisionPage.registerDevice(devToKill);


        String rowXpath = deviceListPage.decommissionDevice(devToKill);

        String rowText = driver.findElement(By.xpath(rowXpath)).getText();
        assertTrue(rowText.equalsIgnoreCase("DISMESSO") || rowText.contains("DISMESSO"));
    }

    @Test
    @DisplayName("UC9 - Complete Shipment")
    void testCompleteShipment() {
        provisionPage.login("admin", "123456789");
        provisionPage.registerDevice("SN-2028-TEST");
        shipmentPage.createShipment("SH-TEST-005", "Verona", "Beni sensibili test");
        shipmentPage.completeShipment("SH-TEST-005");

        assertTrue(driver.getPageSource().contains("ARCHIVIATA"));
    }

    @AfterAll
    static void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}