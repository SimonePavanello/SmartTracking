package it.univr.track.controller.api;

import it.univr.track.dto.DeviceConfigDTO;
import it.univr.track.entity.Device;
import it.univr.track.service.DeviceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/api/device")
public class DeviceController {

    @Autowired
    private DeviceService deviceService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Device> addDevice(@RequestBody Device device) {
        log.info("Received device registration request: {}", device);
        Device savedDevice = deviceService.registerNewDevice(device.getUuid());
        return ResponseEntity.status(HttpStatus.CREATED).body(savedDevice);
    }

    @GetMapping("/{uid}")
    public ResponseEntity<?> readDeviceConfig(
            @PathVariable("uid") String uuid,
            @RequestHeader("X-API-KEY") String apiKey) {

        log.info("Received device configuration request for uid: {}", uuid);

        Optional<Device> deviceOpt = deviceService.getByUid(uuid);

        if (deviceOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Device not found");
        }

        Device device = deviceOpt.get();

        if (!device.getApiKey().equals(apiKey)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("API Key not valid");
        }

        return ResponseEntity.ok(device);
    }

    @PutMapping("/api/device")
    public ResponseEntity<Boolean> editDevice(@RequestBody DeviceConfigDTO configDto) {
        deviceService.updateConfiguration(configDto); //
        return ResponseEntity.ok(true);
    }

    @DeleteMapping("/{uid}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> decommissionDevice(@PathVariable String uid) {
        deviceService.decommissionDevice(uid);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/api/devices")
    public ResponseEntity<List<Device>> devices() {
        return ResponseEntity.ok(deviceService.getAllDevices()); //
    }


}