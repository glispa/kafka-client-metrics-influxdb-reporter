package com.glispa.kafka.influxdb.metrics.reporter;

import lombok.Getter;
import org.influxdb.annotation.Column;
import org.influxdb.annotation.Measurement;

import java.time.Instant;

@Getter
@Measurement(name = "records-consumed-total")
public class RecordsConsumedTotalDto {
    @Column(name = "time")
    private Instant time;
    @Column(name = "client-id", tag = true)
    private String clientId;
    @Column(name = "group", tag = true)
    private String group;
    @Column(name = "topic", tag = true)
    private String topic;
    @Column(name = "custom-tag", tag = true)
    private String customTag;
    @Column(name = "value")
    private double value;
}
