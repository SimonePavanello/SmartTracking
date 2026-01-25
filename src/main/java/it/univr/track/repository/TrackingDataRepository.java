package it.univr.track.repository;

import it.univr.track.entity.TrackData;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TrackingDataRepository extends JpaRepository<TrackData, Long> {

    List<TrackData> findByShipment_ShipmentId(String shipmentId);
}
