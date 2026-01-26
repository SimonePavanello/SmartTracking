package it.univr.track.service;


import it.univr.track.entity.Device;
import it.univr.track.entity.Shipment;
import it.univr.track.entity.enumeration.DeviceStatus;
import it.univr.track.repository.DeviceRepository;
import it.univr.track.repository.ShipmentRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
public class ShipmentService {

    @Autowired
    private ShipmentRepository shipmentRepository;

    @Autowired
    private DeviceRepository deviceRepository;

    public List<Shipment> getAllShipments() {
        return shipmentRepository.findAll();
    }

    public Shipment createShipment(Shipment shipment) {
        shipment.setActive(true);
        return shipmentRepository.save(shipment);
    }

    public void toggleStatus(Long id) {
        Shipment s = shipmentRepository.findById(id).orElseThrow();
        s.setActive(!s.isActive());
        shipmentRepository.save(s);
    }

    public Shipment getById(Long id) {
        return shipmentRepository.findById(id).orElseThrow();
    }


    @Transactional
    public void associateDeviceToShipment(Long shipmentId, String deviceUid) {

        Shipment shipment = shipmentRepository.findById(shipmentId)
                .orElseThrow(() -> new RuntimeException("Shipment not found with ID: " + shipmentId));

        if (!shipment.isActive()) {
            throw new IllegalStateException("Impossible to associate a device to a shipment that is not active");
        }


        Device device = deviceRepository.findDeviceByUuid(deviceUid)
                .orElseThrow(() -> new RuntimeException("Device not found with UID: " + deviceUid));


        if (!"REGISTERED".equals(device.getStatus().toString())) {
            throw new IllegalStateException("The device is not available for a new shipment");
        }

        device.setShipment(shipment);
        device.setStatus(DeviceStatus.ACTIVE);


        deviceRepository.save(device);

        log.info("Device {} connect successfully to shipment {}", deviceUid, shipment.getShipmentId());
    }

    @Transactional
    public void closeShipment(String shipmentId) {
        log.info("Closing shipment {}", shipmentId);
        Shipment shipment = shipmentRepository.findShipmentByShipmentId(shipmentId).orElseThrow();
        shipment.setActive(false);


        for (Device d : shipment.getDevices()) {
            d.setStatus(DeviceStatus.REGISTERED);
            d.setShipment(null);
        }
        shipmentRepository.save(shipment);

        log.info("Shipment {} closed", shipmentId);
    }
}
