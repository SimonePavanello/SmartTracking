package it.univr.track.repository;

import it.univr.track.entity.Device;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DeviceRepository extends JpaRepository<Device, Long> {


    Optional<Device> findByUid(String uid);
    boolean existsByUid(String uid);

}
