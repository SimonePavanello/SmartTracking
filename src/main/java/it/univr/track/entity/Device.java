package it.univr.track.entity;

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

    // Identificativo univoco dell'hardware (es. Serial Number o UUID)
    @Column(unique = true, nullable = false)
    private String deviceId;

    // Nome o versione del modello (es. "SmartSensor-v1")
    private String model;

    // API Key segreta usata per l'autenticazione del device (Scenario 1)
    // Non deve essere visibile nelle risposte JSON pubbliche per sicurezza
    @Column(unique = true)
    private String apiKey;

    // Stato del dispositivo (es. REGISTERED, ACTIVE, DECOMMISSIONED)
    @Enumerated(EnumType.STRING)
    private DeviceStatus status;

    // Data di registrazione nel sistema tramite provisioning
    private LocalDateTime registrationDate;

    // Ultimo contatto ricevuto dal sensore (per monitorare se è online)
    private LocalDateTime lastSeen;



}
