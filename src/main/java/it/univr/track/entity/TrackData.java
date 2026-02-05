package it.univr.track.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@Getter
@Setter
public class TrackData extends AbstractEntity {

    @ManyToOne
    @JoinColumn(name = "uuid")
    @JsonIgnoreProperties({"shipment",  "apiKey"})
    private Device device;

    @ManyToOne
    @JoinColumn(name = "shipmentId")
    @JsonIgnore
    private Shipment shipment;
    private Double latitude;
    private Double longitude;

    private Double temperature;
    private Double humidity;
    private LocalDateTime timestamp;


}
