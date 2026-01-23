package it.univr.track.repository;

import it.univr.track.entity.Device;
import it.univr.track.entity.enumeration.DeviceStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DeviceRepository extends JpaRepository<Device, Long> {


    Optional<Device> findByUid(String uid);
    List<Device> findDeviceByStatusIs(DeviceStatus deviceStatus);
    boolean existsByUid(String uid);

}
