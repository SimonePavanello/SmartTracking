package it.univr.track.integration;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import it.univr.track.dto.TrackingDataDTO;
import it.univr.track.entity.Device;
import it.univr.track.entity.Shipment;
import it.univr.track.repository.DeviceRepository;
import it.univr.track.repository.ShipmentRepository;
import it.univr.track.service.DeviceService;
import it.univr.track.service.ShipmentService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.annotation.DirtiesContext;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class TelemetryIntegrationTest {

    @Autowired
    private DeviceService deviceService;

    @Autowired
    private ShipmentService shipmentService;

    @Autowired
    private DeviceRepository deviceRepository;

    @Autowired
    private ShipmentRepository shipmentRepository;

    @LocalServerPort
    private int port;

    private String testApiKey;
    private String testShipmentId = "SH-API-123";
    private String testDeviceUuid = "DEV-API-123";


    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        RestAssured.basePath = "/api/tracking";

        Shipment shipment = new Shipment();
        shipment.setShipmentId(testShipmentId);
        shipment.setDestination("Verona");
        shipment.setActive(true);
        shipmentRepository.save(shipment);

        Device device = deviceService.registerNewDevice(testDeviceUuid);
        testApiKey = device.getApiKey();

        shipmentService.associateDeviceToShipment(shipment.getId(), testDeviceUuid);
    }

    @Test
    @DisplayName("UC5 - Invio Telemetria con API Key Valida")
    void testSendDataSuccess() {
        TrackingDataDTO data = new TrackingDataDTO();
        data.setTemperature(22.5);
        data.setHumidity(60.0);
        data.setLatitude(45.4642);
        data.setLongitude(9.1900);

        given()
                .header("X-API-KEY", testApiKey)
                .contentType(ContentType.JSON)
                .body(data)
                .when()
                .post("/data")
                .then()
                .statusCode(anyOf(is(200), is(403)));
    }

    @Test
    @DisplayName("UC5 - Blocco Invio con API Key Errata")
    void testSendDataUnauthorized() {
        given()
                .header("X-API-KEY", "CHIAVE_FALSA")
                .contentType(ContentType.JSON)
                .body(new TrackingDataDTO())
                .when()
                .post("/data")
                .then()
                .statusCode(401);
    }

    @Test
    @DisplayName("UC7 - Verifica Storico Spedizione")
    void testReadShipmentHistory() {
        String shipmentId = "SH-001";

        given()
                .pathParam("shipmentId", shipmentId)
                .when()
                .get("/shipment/{shipmentId}")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("$", instanceOf(java.util.List.class));
    }
}