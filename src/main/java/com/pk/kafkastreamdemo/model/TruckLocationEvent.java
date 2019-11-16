package com.pk.kafkastreamdemo.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class TruckLocationEvent {
    private String location;
    private String truckNumber;
    private long longitude;
    private long latitude;
}