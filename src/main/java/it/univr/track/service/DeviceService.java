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
import java.util.UUID;

@Slf4j
@Service
public class DeviceService {

    @Autowired
    private DeviceRepository deviceRepository;

    @Transactional
    public Device registerNewDevice(String uid) {
        if (deviceRepository.existsByUid(uid)) {
            throw new RuntimeException("Dispositivo già registrato");
        }
        Device device = new Device();
        device.setUid(uid);
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
                .orElseThrow(() -> new RuntimeException("Device non trovato"));
    }

    @Transactional
    public void decommissionDevice(Long id) {
        Device device = getById(id);
        device.setShipment(null);
        device.setStatus(DeviceStatus.DECOMMISSIONED);
        deviceRepository.save(device);
    }

    @Transactional
    public void updateConfiguration(DeviceConfigDTO config) {
        Device device = getById(config.getDeviceId());
        device.setSamplingIntervalSeconds(config.getInterval());
        deviceRepository.save(device);
    }

    public boolean pushConfigToHardware(Long id) {
        Device device = getById(id);
        // Qui andrebbe la logica di integrazione IoT (es. MQTT o HTTP call)
        log.info("Configurazione inviata al device " + device.getUid());
        return true;
    }

    public List<Device> getReadyDevices(){
        List<Device> deviceByStatusIs = deviceRepository.findDeviceByStatusIs(DeviceStatus.REGISTERED);
        log.info("Devices found {}", deviceByStatusIs.size());
        return deviceByStatusIs;
    }

}
