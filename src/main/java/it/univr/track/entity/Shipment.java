package it.univr.track.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@NoArgsConstructor
@Setter
@Getter
public class Shipment extends AbstractEntity {

    @Column(unique = true, nullable = false)
    private String shipmentId;

    private String description;
    private String destination;

    private boolean active;

    @OneToMany(mappedBy = "shipment", cascade = CascadeType.ALL)

    private List<Device> devices;


}
