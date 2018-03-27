package com.glispa.kafka.influxdb.metrics.reporter;

import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class CustomTagsPropertyParser {

    Map<String, String> parseMapProperty(String map) {
        if (map.isEmpty()) {
            return Collections.emptyMap();
        }
        return Stream.of(map.split(","))
                     .collect(Collectors.toMap(e -> e.split("=")[0], e -> e.split("=")[1]));
    }
}
