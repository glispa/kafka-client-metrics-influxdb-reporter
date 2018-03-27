Kafka Client Metrics InfluxDB Reporter
=====================================
This library provides an ability to send Kafka client metrics into InfluxDB.
It utilises Kafka's `MetricsReporter` listener interface to get client metrics and
[influxdb-java](https://github.com/influxdata/influxdb-java) library to send them into
InfluxDB after some basic conversion.

Reporter was tested to work with Kafka clients 1.0.0 and Influxdb 1.3.9.

# Installation

Add the following dependency to your `pom.xml`
```
<dependency>
    <groupId>com.glispa</groupId>
    <artifactId>kafka-client-metrics-influxdb-reporter</artifactId>
    <version>${put-required-version-here}</version>
</dependency>
```

# Configuration
Firstly you need to add Kafka property to enable this reporter. For example, for Kafka consumer:
```
props.put(ConsumerConfig.METRIC_REPORTER_CLASSES_CONFIG, "com.glispa.kafka.influxdb.metrics.reporter.InfluxdbKafkaMetricsReporter");
```

To configure the repoter use the following properties. Property names are available as public string constants in
[InfluxdbConfig](src/main/java/com/glispa/kafka/influxdb/metrics/reporter/InfluxdbConfig.java) class.

|Property name |Default value |Type |Description |
|--------------|--------------|:-----:|------------|
|INFLUXDB_SERVER_CONFIG|`"localhost:8086"`|String|InfluxDB endpoint|
|INFLUXDB_DATABASE_CONFIG|`"database"`|String|InfluxDB database|
|INFLUXDB_USER_CONFIG|`"user"`|String|InfluxDB user|
|INFLUXDB_PASSWORD_CONFIG|`"password"`|String|InfluxDB user password|
|INFLUXDB_RETENTION_POLICY_CONFIG|`"autogen"`|String|Influxdb retention policy name|
|INFLUXDB_INTEVAL_SEC_CONFIG|`60`|int|Interval for sending the metrics in secods|
|INFLUXDB_METRICS_NAME_PREFIX_CONFIG|`""`|String|Prefix to append to each influxdb measurement with dot (`.`) as separator|
|INFLUXDB_CUSTOM_TAGS_CONFIG|`""`|String|Map of custom tags to add to all measurements, format key1=value1,key2=value2|
|INFLUXDB_METRICS_WHITELIST_REGEX_CONFIG|`""`|String|Java regex to send **only** metrics with matching names|
|INFLUXDB_METRICS_BLACKLIST_REGEX_CONFIG|`""`|String|Java regex to do **NOT** send metrics with matching names|


Check the example with full configuration below:

```
props.put(ConsumerConfig.METRIC_REPORTER_CLASSES_CONFIG, "com.glispa.kafka.influxdb.metrics.reporter.InfluxdbKafkaMetricsReporter");
props.put(InfluxdbConfig.INFLUXDB_SERVER_CONFIG, "your.influxdb.host:8086");
props.put(InfluxdbConfig.INFLUXDB_USER_CONFIG, "influxdb_user");
props.put(InfluxdbConfig.INFLUXDB_PASSWORD_CONFIG, "influxdb_user_password");
props.put(InfluxdbConfig.INFLUXDB_DATABASE_CONFIG, "influxdb_database");
props.put(InfluxdbConfig.INFLUXDB_INTEVAL_SEC_CONFIG, 60);
props.put(InfluxdbConfig.INFLUXDB_CUSTOM_TAGS_CONFIG, "tag1=value1,tag2=value2");
props.put(InfluxdbConfig.INFLUXDB_RETENTION_POLICY_CONFIG, "default");
props.put(InfluxdbConfig.INFLUXDB_METRICS_NAME_PREFIX_CONFIG, "prefix");
props.put(InfluxdbConfig.INFLUXDB_METRICS_WHITELIST_REGEX_CONFIG, "records-consumed-rate|records-lag-max");
props.put(InfluxdbConfig.INFLUXDB_METRICS_BLACKLIST_REGEX_CONFIG, "");
```

# Development stack
* `Java 8`, `Maven`
* [Spock](https://github.com/spockframework/spock) for tests
* `docker` and `docker-compose` for running tests locally

```
# Start external dependencies in docker containers with docker-compose
./scripts/compose-up.sh
# Run tests
mvn clean test
# Stop docker containers
./scripts/compose-down.sh
```

## License

[MIT License](LICENSE)
