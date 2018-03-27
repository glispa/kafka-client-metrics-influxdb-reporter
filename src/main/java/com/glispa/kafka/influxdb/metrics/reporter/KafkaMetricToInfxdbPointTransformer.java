package com.glispa.kafka.influxdb.metrics.reporter;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.common.Metric;
import org.apache.kafka.common.MetricName;
import org.influxdb.dto.Point;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
class KafkaMetricToInfxdbPointTransformer {

    @NonNull
    private final String prefix;
    private final Map<String, String> customTags;

    Optional<Point> transform(Metric metric) {
        MetricName metricName = metric.metricName();
        Object value = metric.metricValue();
        Point.Builder builder = Point.measurement(measurmentName(metricName))
                                     .time(System.currentTimeMillis(), TimeUnit.MILLISECONDS);

        if (value instanceof Number) {
            double doubleValue = ((Number)value).doubleValue();
            if (Double.isFinite(doubleValue)) {
                builder.addField("value", doubleValue);
            } else {
                return Optional.empty();
            }
        } else {
            builder.addField("value", value.toString());
        }

        metricName.tags().forEach(builder::tag);

        customTags.forEach(builder::tag);

        builder.tag("group", metricName.group());
        return Optional.of(builder.build());

    }

    private String measurmentName(MetricName metricName) {
        String originalName = metricName.name();
        if (!prefix.isEmpty()) {
            return String.format("%s.%s", prefix, originalName);
        } else {
            return originalName;
        }

    }
}
