package com.pk.kafkastreamdemo.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TruckDashboard {
    private String truckNumber;
    private String driverName;
    private long longitude;
    private long latitude;
}