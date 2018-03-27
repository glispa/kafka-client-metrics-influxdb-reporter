package com.glispa.kafka.influxdb.metrics.reporter;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.metrics.KafkaMetric;
import org.apache.kafka.common.metrics.MetricsReporter;
import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static com.glispa.kafka.influxdb.metrics.reporter.InfluxdbConfig.*;

/**
 * This class sends Kafka client metrics to InfluxDB.
 * For different configuration options check {@link com.glispa.kafka.influxdb.metrics.reporter.InfluxdbConfig}.
 */
@Slf4j
public class InfluxdbKafkaMetricsReporter implements MetricsReporter {

    private final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
    private final List<KafkaMetric> metricList = Collections.synchronizedList(new ArrayList<KafkaMetric>());
    private KafkaMetricToInfxdbPointTransformer transformer;
    private MetricFilter filter;
    private InfluxdbConfig config;
    private InfluxDB influxDB;

    @Override
    public void init(List<KafkaMetric> metrics) {
        metricList.addAll(metrics);
        int interval = config.getInt(INFLUXDB_INTEVAL_SEC_CONFIG);
        executor.scheduleAtFixedRate(this::report, interval, interval, TimeUnit.SECONDS);
    }

    @Override
    public void metricChange(KafkaMetric metric) {
        metricList.add(metric);
    }

    @Override
    public void metricRemoval(KafkaMetric metric) {
        metricList.remove(metric);
    }

    @Override
    public void close() {
        executor.submit(this::report);
        executor.shutdown();
        try {
            boolean terminated = executor.awaitTermination(5, TimeUnit.SECONDS);
            if (!terminated) {
                log.error("Timed out before executor termination!");
            }
        } catch (InterruptedException e) {
            log.error("Can not shutdown executor gracefully", e);
        }

        influxDB.close();
    }

    private void report() {
        metricList.stream()
                  .filter(filter::isAllowed)
                  .map(transformer::transform)
                  .filter(Optional::isPresent)
                  .map(Optional::get)
                  .forEach(influxDB::write);
    }

    @Override
    public void configure(Map<String, ?> configs) {
        this.config = new InfluxdbConfig(configs);
        String url = String.format("http://%s", config.getString(INFLUXDB_SERVER_CONFIG));
        String username = config.getString(INFLUXDB_USER_CONFIG);
        String password = config.getPassword(INFLUXDB_PASSWORD_CONFIG).value();
        this.influxDB = InfluxDBFactory.connect(url, username, password);
        influxDB.setDatabase(config.getString(INFLUXDB_DATABASE_CONFIG));
        influxDB.enableBatch(100, 3, TimeUnit.SECONDS);
        influxDB.setRetentionPolicy(config.getString(INFLUXDB_RETENTION_POLICY_CONFIG));

        String prefix = config.getString(INFLUXDB_METRICS_NAME_PREFIX_CONFIG);
        String customTagsUnparsed = config.getString(INFLUXDB_CUSTOM_TAGS_CONFIG);
        CustomTagsPropertyParser parser = new CustomTagsPropertyParser();
        Map<String, String> customTags = parser.parseMapProperty(customTagsUnparsed);
        this.transformer = new KafkaMetricToInfxdbPointTransformer(prefix, customTags);

        String whitelist = config.getString(INFLUXDB_METRICS_WHITELIST_REGEX_CONFIG);
        String blacklist = config.getString(INFLUXDB_METRICS_BLACKLIST_REGEX_CONFIG);
        this.filter = new MetricFilter(whitelist, blacklist);
    }
}