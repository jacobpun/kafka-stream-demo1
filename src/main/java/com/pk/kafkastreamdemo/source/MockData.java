package com.pk.kafkastreamdemo.source;

import java.util.List;

/**
 * MockData
 */
public interface MockData {
    List<String> TRUCK_NUMBERS = List.of("trk1", "trk2", "trk3", "trk4", "trk5");
    List<String> DRIVERS = List.of("Tom", "Jane", "Matt", "Bill", "Janet");
}