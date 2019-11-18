package com.pk.kafkastreamdemo;

import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
import org.springframework.cloud.stream.binder.kafka.streams.InteractiveQueryService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerRequest;
import org.springframework.web.servlet.function.ServerResponse;

import lombok.AllArgsConstructor;

import static org.springframework.web.servlet.function.ServerResponse.ok;
import static org.springframework.web.servlet.function.ServerResponse.accepted;
import static org.springframework.web.servlet.function.RouterFunctions.route;

import java.util.Map;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import com.pk.kafkastreamdemo.model.AssignDriverEvent;
import com.pk.kafkastreamdemo.model.TruckDashboard;
import com.pk.kafkastreamdemo.service.TruckService;


@Configuration
@AllArgsConstructor
public class RouteConfig {
    private final InteractiveQueryService queryService;
    private final TruckService truckService;

    @Bean
    public RouterFunction<ServerResponse> routes() {
        return route()
            .POST("/assign-driver", this::assignDriver)
            .GET("/dashboard", this::fetchDashboard)
            .build();
    }

    public ServerResponse fetchDashboard(final ServerRequest req) {
        ReadOnlyKeyValueStore<String, TruckDashboard> store = this.queryService
                .getQueryableStore(AnalyticsBinding.TRUCK_DASHBOARD_MV, QueryableStoreTypes.keyValueStore());
        Map<String, TruckDashboard> dashboard = StreamSupport
                .stream(Spliterators.spliteratorUnknownSize(store.all(), Spliterator.ORDERED), false)
                .collect(Collectors.toMap(kv -> kv.key, kv -> kv.value));
        return ok().body(dashboard);
    }

    public ServerResponse assignDriver(final ServerRequest req) {
        try {
            AssignDriverEvent body = req.body(AssignDriverEvent.class);
            this.truckService.assignDriver(body);
            return accepted().build();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}