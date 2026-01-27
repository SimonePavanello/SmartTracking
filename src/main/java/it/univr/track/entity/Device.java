package it.univr.track.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import it.univr.track.entity.enumeration.DeviceStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@NoArgsConstructor
@Setter
@Getter
public class Device extends AbstractEntity {

    @ManyToOne
    private Shipment shipment;

    @Column(unique = true, nullable = false)
    private String uuid;

    @Column(unique = true)
    @JsonIgnore
    private String apiKey;

    @Enumerated(EnumType.STRING)
    private DeviceStatus status;

    private Integer samplingIntervalSeconds;


}
