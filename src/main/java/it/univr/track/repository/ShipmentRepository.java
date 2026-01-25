package it.univr.track.repository;

import it.univr.track.entity.Shipment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ShipmentRepository extends JpaRepository<Shipment, Long> {
    Optional<Shipment> findShipmentByShipmentId(String code);
}
