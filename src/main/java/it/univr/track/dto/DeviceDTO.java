package it.univr.track.dto;


import lombok.Getter;

@Getter
public class DeviceDTO {

    private String deviceId;
    private String modelName;

    private Integer frequency;
    private Double tempMax;
    private Double shockThreshold;
    private String status;
}
