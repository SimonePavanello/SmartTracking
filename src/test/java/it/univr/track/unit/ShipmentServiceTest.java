package it.univr.track.unit;

import it.univr.track.entity.Device;
import it.univr.track.entity.Shipment;
import it.univr.track.entity.enumeration.DeviceStatus;
import it.univr.track.repository.DeviceRepository;
import it.univr.track.repository.ShipmentRepository;
import it.univr.track.service.ShipmentService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ShipmentServiceTest {

    @Mock
    private ShipmentRepository shipmentRepository;

    @Mock
    private DeviceRepository deviceRepository;

    @InjectMocks
    private ShipmentService shipmentService;

    @Test
    @DisplayName("Creazione spedizione - Deve impostare lo stato active a true")
    void testCreateShipment() {
        Shipment shipment = new Shipment();
        shipment.setShipmentId("SH-001");

        when(shipmentRepository.save(any(Shipment.class))).thenAnswer(i -> i.getArguments()[0]);

        Shipment created = shipmentService.createShipment(shipment);

        assertTrue(created.isActive());
        verify(shipmentRepository).save(shipment);
    }

    @Test
    @DisplayName("UC4 - Associazione Device - Successo")
    void testAssociateDeviceSuccess() {
        Long shipmentId = 1L;
        String deviceUid = "SN-TEST";

        Shipment shipment = new Shipment();
        shipment.setActive(true);

        Device device = new Device();
        device.setUuid(deviceUid);
        device.setStatus(DeviceStatus.REGISTERED);

        when(shipmentRepository.findById(shipmentId)).thenReturn(Optional.of(shipment));
        when(deviceRepository.findDeviceByUuid(deviceUid)).thenReturn(Optional.of(device));

        shipmentService.associateDeviceToShipment(shipmentId, deviceUid);

        assertEquals(DeviceStatus.ACTIVE, device.getStatus());
        assertEquals(shipment, device.getShipment());
        verify(deviceRepository).save(device);
    }

    @Test
    @DisplayName("UC4 - Associazione Device - Fallisce se spedizione non attiva")
    void testAssociateDeviceFailInactiveShipment() {
        Long shipmentId = 1L;
        Shipment shipment = new Shipment();
        shipment.setActive(false);

        when(shipmentRepository.findById(shipmentId)).thenReturn(Optional.of(shipment));

        assertThrows(IllegalStateException.class, () ->
                shipmentService.associateDeviceToShipment(shipmentId, "SN-TEST"));
    }

    @Test
    @DisplayName("UC9 - Chiusura Spedizione - Deve liberare i sensori")
    void testCloseShipment() {
        String shipmentId = "SH-100";
        Shipment shipment = new Shipment();
        shipment.setShipmentId(shipmentId);
        shipment.setActive(true);

        Device d1 = new Device();
        d1.setStatus(DeviceStatus.ACTIVE);
        d1.setShipment(shipment);

        shipment.setDevices(new ArrayList<>(List.of(d1)));

        when(shipmentRepository.findShipmentByShipmentId(shipmentId)).thenReturn(Optional.of(shipment));

        shipmentService.closeShipment(shipmentId);

        assertFalse(shipment.isActive());
        assertEquals(DeviceStatus.REGISTERED, d1.getStatus());
        assertNull(d1.getShipment());
        verify(shipmentRepository).save(shipment);
    }
}
