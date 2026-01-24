package it.univr.track.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
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
    private Device device;
    private Double latitude;
    private Double longitude;
    private LocalDateTime timestamp;


}
