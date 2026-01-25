package it.univr.track.dto;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class TrackingDataDTO {

    private double temperature;
    private double humidity;
    private double latitude;
    private double longitude;
}
