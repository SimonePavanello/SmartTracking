package it.univr.track.controller.api;

import it.univr.track.dto.DeviceDTO;
import it.univr.track.entity.enumeration.DeviceStatus;
import it.univr.track.repository.DeviceRepository;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import it.univr.track.entity.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;


@Slf4j
@Controller
public class DeviceController {

    @Autowired
    private DeviceRepository deviceRepository;

    // add new device
    @PostMapping("/api/device")
    @ResponseBody
    public boolean addDevice(@RequestBody DeviceDTO deviceDto, HttpSession session) {

        log.info("Adding device: " + deviceDto.getDeviceId());
        UserRegistered user = (UserRegistered) session.getAttribute("loggedInUser");
        if (user == null || !user.getRole().name().equals("ADMIN")) {
            log.error("Unauthorized access to device registration page");
            throw new RuntimeException("Unauthorized");
        }

        if (deviceRepository.existsByDeviceId(deviceDto.getDeviceId())) {
            return false;
        }

        Device device = new Device();
        device.setDeviceId(deviceDto.getDeviceId());
        device.setModel(deviceDto.getModelName());
        device.setApiKey(UUID.randomUUID().toString());
        device.setRegistrationDate(LocalDateTime.now());

        deviceRepository.save(device);

        log.info("Device added successfully");

        return true;
    }

    // read the device configuration
    @GetMapping("/device/{deviceId}")
    public Device readDeviceConfig(@PathVariable("deviceId") String deviceId) {
        log.info("Reading device configuration: " + deviceId);
        return deviceRepository.findByDeviceId(deviceId)
                .orElse(new Device());
    }

    // update device configuration
    @PutMapping("/api/device")
    @ResponseBody
    public boolean editDevice(@RequestBody DeviceDTO updatedDevice) {
        log.info("Updating device: " + updatedDevice.getDeviceId());
        return deviceRepository.findByDeviceId(updatedDevice.getDeviceId()).map(device -> {
            device.setFrequency(updatedDevice.getFrequency());
            device.setTempMax(updatedDevice.getTempMax());
            device.setShockThreshold(updatedDevice.getShockThreshold());
            device.setStatus(DeviceStatus.valueOf(updatedDevice.getStatus()));
            deviceRepository.save(device);
            log.info("Device updated successfully");
            return true;
        }).orElse(false);
    }

    // decommission a device
    @DeleteMapping("/api/device")
    public boolean deleteDevice() {
        return true;
    }

    // list all the devices that are visible for this user
    @GetMapping("/api/devices")
    @ResponseBody
    public List<Device> devices() {
        return deviceRepository.findAll();
    }


}