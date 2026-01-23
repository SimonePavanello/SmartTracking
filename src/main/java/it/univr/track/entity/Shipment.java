package it.univr.track.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@NoArgsConstructor
@Setter
@Getter
public class Shipment extends AbstractEntity {

    @Column(unique = true, nullable = false)
    private String code; // Es: SH-2026-001

    private String description;
    private String destination;

    private boolean active; // Se false, il sistema rifiuterà i dati dai sensori

    @OneToMany
    private List<Device> devices;


}
