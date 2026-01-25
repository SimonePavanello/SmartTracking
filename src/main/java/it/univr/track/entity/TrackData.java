package it.univr.track.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import it.univr.track.entity.enumeration.Gender;
import it.univr.track.entity.enumeration.Role;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@NoArgsConstructor
@Getter
@Setter
public class TrackData extends AbstractEntity {

    @ManyToOne
    @JoinColumn(name = "uuid")
    private Device device;

    @ManyToOne
    @JoinColumn(name = "shipmentId")
    private Shipment shipment;
    private Double latitude;
    private Double longitude;

    private Double temperature;
    private Double humidity;
    private LocalDateTime timestamp;


}
