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
import static org.springframework.web.servlet.function.RouterFunctions.route;

import java.util.Map;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@AllArgsConstructor
@Configuration
public class RouteConfig {
    private final InteractiveQueryService queryService;
    
    @Bean
    public RouterFunction<ServerResponse> routes() {
        return route().GET("/count", this::fetchCount).build();
    }

    public ServerResponse fetchCount(final ServerRequest req) {
        ReadOnlyKeyValueStore<String, Long> store = this.queryService.getQueryableStore(AnalyticsBinding.US_TRUCK_COUNT_MV,
                QueryableStoreTypes.keyValueStore());
        Map<String, Long> countPerTruck = StreamSupport
                .stream(Spliterators.spliteratorUnknownSize(store.all(), Spliterator.ORDERED), false)
                .collect(Collectors.toMap(kv -> kv.key, kv -> kv.value));
        return ok().body(countPerTruck);
    }
}