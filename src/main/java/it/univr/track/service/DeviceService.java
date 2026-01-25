package it.univr.track.service;


import it.univr.track.dto.DeviceConfigDTO;
import it.univr.track.entity.Device;
import it.univr.track.entity.enumeration.DeviceStatus;
import it.univr.track.repository.DeviceRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
public class DeviceService {

    @Autowired
    private DeviceRepository deviceRepository;

    @Transactional
    public Device registerNewDevice(String uuid) {
        if (deviceRepository.existsDeviceByUuid(uuid)) {
            throw new RuntimeException("Device already registered with uuid: " + uuid);
        }
        Device device = new Device();
        device.setUuid(uuid);
        device.setStatus(DeviceStatus.REGISTERED);
        device.setApiKey(UUID.randomUUID().toString());
        device.setSamplingIntervalSeconds(60); // Default config
        return deviceRepository.save(device);
    }

    public List<Device> getAllDevices() {
        return deviceRepository.findAll();
    }

    public Device getById(Long id) {
        return deviceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Device not found"));
    }
    public Optional<Device> findByApiKey(String apiKey) {
        return deviceRepository.findDeviceByApiKey(apiKey);
    }


    public Optional<Device> getByUid(String uuid) {
        log.info("Device with UUID: {}", uuid);
        return deviceRepository.findDeviceByUuid(uuid);
    }

    @Transactional
    public void decommissionDevice(String uuid) {
        Optional<Device> device = getByUid(uuid);
        if (device.isEmpty()) {
            throw new RuntimeException("Device not found");
        }else {
            device.get().setShipment(null);
            device.get().setStatus(DeviceStatus.DECOMMISSIONED);
            deviceRepository.save(device.get());
        }


    }

    @Transactional
    public void updateConfiguration(DeviceConfigDTO config) {
        log.info("Received configuration update request: {} with device id: {}", config, config.getUuid());
        Device device = getByUid(config.getUuid()).get();
        device.setSamplingIntervalSeconds(config.getInterval());
        deviceRepository.save(device);
    }

    public boolean pushConfigToHardware(String uuid) {
        Optional<Device> device = getByUid(uuid);
        if (device.isEmpty()){
            return false;
        }
        // Qui andrebbe la logica di integrazione IoT (es. MQTT o HTTP call)
        log.info("Sending configuration to device {}", device.get().getUuid());
        return true;
    }

    public List<Device> getReadyDevices() {
        List<Device> deviceByStatusIs = deviceRepository.findDeviceByStatusIs(DeviceStatus.REGISTERED);
        log.info("Devices found {}", deviceByStatusIs.size());
        return deviceByStatusIs;
    }

}
