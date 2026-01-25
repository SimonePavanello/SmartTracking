package it.univr.track.controller.api;

import it.univr.track.dto.TrackingDataDTO;
import it.univr.track.entity.enumeration.DeviceStatus;
import it.univr.track.repository.TrackingDataRepository;
import it.univr.track.service.DeviceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import it.univr.track.entity.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;


@Slf4j
@RestController
@RequestMapping("/api/tracking")
public class TrackDataController {

    @Autowired
    private DeviceService deviceService;
    @Autowired
    private TrackingDataRepository trackingDataRepository;

    @PostMapping("/data")
    public ResponseEntity<?> writeData(@RequestHeader("X-API-KEY") String apiKey,
                                       @RequestBody TrackingDataDTO dto) {
        Device device = deviceService.findByApiKey(apiKey)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));

        if (device.getStatus() != DeviceStatus.ACTIVE || device.getShipment() == null) {
            return ResponseEntity.badRequest().body("Device not active or not associated to a shipment");
        }

        TrackData data = new TrackData();
        data.setTemperature(dto.getTemperature());
        data.setHumidity(dto.getHumidity());
        data.setLatitude(dto.getLatitude());
        data.setLongitude(dto.getLongitude());
        data.setTimestamp(LocalDateTime.now());
        data.setDevice(device);
        data.setShipment(device.getShipment());

        trackingDataRepository.save(data);
        log.info("Data received");
        return ResponseEntity.ok("Data received");
    }

    @GetMapping("/shipment/{shipmentId}")
    public List<TrackData> readDataByShipment(@PathVariable("shipmentId") String shipmentId) {
        log.info("Read data for shipment {}", shipmentId);
        return trackingDataRepository.findByShipment_ShipmentId(shipmentId);
    }

}
