package it.univr.track.controller.api;

import it.univr.track.dto.DeviceConfigDTO;
import it.univr.track.dto.DeviceDTO;
import it.univr.track.entity.Device;
import it.univr.track.service.DeviceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@Controller
public class DeviceController {

    @Autowired
    private DeviceService deviceService;

    @PostMapping("/api/device")
    public ResponseEntity<Boolean> addDevice(@RequestBody DeviceDTO dto) {
        log.info("API: Ricevuta richiesta di provisioning per UID: {}", dto.getUid());
        deviceService.registerNewDevice(dto.getUid()); //
        return ResponseEntity.ok(true);
    }

    @GetMapping("/api/device/{deviceId}")
    public ResponseEntity<Device> readDeviceConfig(@PathVariable("deviceId") Long id) {
        return ResponseEntity.ok(deviceService.getById(id)); //
    }

    @PutMapping("/api/device")
    public ResponseEntity<Boolean> editDevice(@RequestBody DeviceConfigDTO configDto) {
        deviceService.updateConfiguration(configDto); //
        return ResponseEntity.ok(true);
    }

    @DeleteMapping("/api/device/{id}")
    public ResponseEntity<Boolean> deleteDevice(@PathVariable Long id) {
        deviceService.decommissionDevice(id); //
        return ResponseEntity.ok(true);
    }

    @GetMapping("/api/devices")
    public ResponseEntity<List<Device>> devices() {
        // Implementa il filtro per utente se necessario
        return ResponseEntity.ok(deviceService.getAllDevices()); //
    }


}