package it.univr.track.unit;

import it.univr.track.dto.DeviceConfigDTO;
import it.univr.track.entity.Device;
import it.univr.track.entity.enumeration.DeviceStatus;
import it.univr.track.repository.DeviceRepository;
import it.univr.track.service.DeviceService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeviceServiceTest {

    @Mock
    private DeviceRepository deviceRepository;

    @InjectMocks
    private DeviceService deviceService;

    private String testUuid = "SN-2026-TEST";

    @Test
    @DisplayName("Register New Device - Success")
    void testRegisterNewDeviceSuccess() {
        when(deviceRepository.existsDeviceByUuid(testUuid)).thenReturn(false);
        when(deviceRepository.save(any(Device.class))).thenAnswer(i -> i.getArguments()[0]);

        Device result = deviceService.registerNewDevice(testUuid);

        assertNotNull(result);
        assertEquals(testUuid, result.getUuid());
        assertEquals(DeviceStatus.REGISTERED, result.getStatus());
        assertNotNull(result.getApiKey());
        assertEquals(60, result.getSamplingIntervalSeconds());
        verify(deviceRepository).save(any(Device.class));
    }

    @Test
    @DisplayName("Register New Device - Error if Already Exists")
    void testRegisterNewDeviceFail() {
        when(deviceRepository.existsDeviceByUuid(testUuid)).thenReturn(true);

        assertThrows(RuntimeException.class, () -> deviceService.registerNewDevice(testUuid));
    }

    @Test
    @DisplayName("Decommissioning device - Success")
    void testDecommissionDeviceSuccess() {
        Device device = new Device();
        device.setUuid(testUuid);
        device.setStatus(DeviceStatus.ACTIVE);

        when(deviceRepository.findDeviceByUuid(testUuid)).thenReturn(Optional.of(device));

        deviceService.decommissionDevice(testUuid);

        assertEquals(DeviceStatus.DECOMMISSIONED, device.getStatus());
        assertNull(device.getShipment());
        verify(deviceRepository).save(device);
    }

    @Test
    @DisplayName("UC8 - Decommissioning Failed (Device Not Found)")
    void testDecommissionDeviceFailed() {
        String invalidUuid = "NON-EXISTENT-ID";
        when(deviceRepository.findDeviceByUuid(invalidUuid)).thenReturn(Optional.empty());

        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () -> {
            deviceService.decommissionDevice(invalidUuid);
        });

        assertEquals("Device not found with UUID: NON-EXISTENT-ID", exception.getMessage());

        verify(deviceRepository, never()).save(any(Device.class));
    }

    @Test
    @DisplayName("Update Configuration - Success")
    void testUpdateConfiguration() {
        Device device = new Device();
        device.setUuid(testUuid);
        device.setSamplingIntervalSeconds(60);

        DeviceConfigDTO config = new DeviceConfigDTO();
        config.setUuid(testUuid);
        config.setInterval(120);

        when(deviceRepository.findDeviceByUuid(testUuid)).thenReturn(Optional.of(device));

        deviceService.updateConfiguration(config);

        assertEquals(120, device.getSamplingIntervalSeconds());
        verify(deviceRepository).save(device);
    }

    @Test
    @DisplayName("Push Configuration - Device Not Found")
    void testPushConfigFail() {
        when(deviceRepository.findDeviceByUuid(testUuid)).thenReturn(Optional.empty());
        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () -> {
            deviceService.pushConfigToHardware(testUuid);
        });
    }
}
