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
        shipment.setActive(true); // Di default una nuova spedizione è attiva
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
        // 1. Recupero la spedizione
        Shipment shipment = shipmentRepository.findById(shipmentId)
                .orElseThrow(() -> new RuntimeException("Spedizione non trovata con ID: " + shipmentId));

        // 2. Recupero il device tramite UID (quello del QR-code)
        Device device = deviceRepository.findDeviceByUuid(deviceUid)
                .orElseThrow(() -> new RuntimeException("Device non trovato con UID: " + deviceUid));

        // 3. Controllo logico: il device deve essere libero (REGISTERED)
        if (!"REGISTERED".equals(device.getStatus().toString())) {
            throw new IllegalStateException("Il dispositivo non è disponibile per una nuova spedizione");
        }

        // 4. Eseguo l'associazione bidirezionale
        device.setShipment(shipment);
        device.setStatus(DeviceStatus.ACTIVE); // Cambio stato

        // Salvataggio (gestito da @Transactional)
        deviceRepository.save(device);

        log.info("Device {} associato con successo alla spedizione {}", deviceUid, shipment.getCode());
    }
}
