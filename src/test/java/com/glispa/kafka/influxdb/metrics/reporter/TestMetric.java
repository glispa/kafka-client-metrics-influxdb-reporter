package com.glispa.kafka.influxdb.metrics.reporter;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.common.Metric;
import org.apache.kafka.common.MetricName;

import java.util.Collections;

@RequiredArgsConstructor
class TestMetric implements Metric {
    private static final String NO_MATTER = "no-matter";
    private final String name;

    @Override
    public MetricName metricName() {
        return new MetricName(name, NO_MATTER, NO_MATTER, Collections.emptyMap());
    }

    @Override
    public double value() {
        return 0;
    }

    @Override
    public Object metricValue() {
        return 0;
    }
}
