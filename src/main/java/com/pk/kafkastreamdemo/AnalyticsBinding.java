package com.pk.kafkastreamdemo;

import com.pk.kafkastreamdemo.model.AssignDriverEvent;
import com.pk.kafkastreamdemo.model.TruckDashboard;
import com.pk.kafkastreamdemo.model.TruckLocationEvent;

import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;

public interface AnalyticsBinding {
    public String TRUCK_LOCATION_OUT = "tlout";
    public String TRUCK_LOCATION_IN = "tlin";

    public String ASSIGN_DRIVER_OUT = "drasgnout";
    public String ASSIGN_DRIVER_IN = "drasgnin";

    public String TRUCK_DASHBOARD_OUT = "tdshbrdout";
    public String TRUCK_DASHBOARD_IN = "tdshbrdin";
    
	public String TRUCK_DASHBOARD_MV = "dashboardmv";
    
    @Input(TRUCK_LOCATION_IN)
    public KStream<String, TruckLocationEvent> truckLocationIn();

    @Output(TRUCK_LOCATION_OUT)
    public MessageChannel truckLocationOut();

    @Input(ASSIGN_DRIVER_IN)
    public KStream<String, AssignDriverEvent> assignDriverIn();

    @Output(ASSIGN_DRIVER_OUT)
    public MessageChannel assignDriverOut();

    @Output(TRUCK_DASHBOARD_OUT)
    public KStream<String, TruckDashboard> truckDashboardOut();

    @Input(TRUCK_DASHBOARD_IN)
    public KTable<String, TruckDashboard> truckDashboardIn();
}