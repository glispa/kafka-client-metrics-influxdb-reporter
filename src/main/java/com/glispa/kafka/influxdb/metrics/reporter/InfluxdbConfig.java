package com.glispa.kafka.influxdb.metrics.reporter;

import org.apache.kafka.common.config.AbstractConfig;
import org.apache.kafka.common.config.ConfigDef;

import java.util.Map;

import static org.apache.kafka.common.config.ConfigDef.Importance.HIGH;
import static org.apache.kafka.common.config.ConfigDef.Importance.LOW;
import static org.apache.kafka.common.config.ConfigDef.Importance.MEDIUM;
import static org.apache.kafka.common.config.ConfigDef.Type.*;

/**
 * Configuration of InfluxDB reporter for Kafka client metrics.
 */
public class InfluxdbConfig extends AbstractConfig {

    /**
     * Property name for setting InfluxDB host and port.
     */
    public static final String INFLUXDB_SERVER_CONFIG = "kafka.influxdb.metrics.server";
    private static final String INFLUXDB_SERVER_DEFAULT = "localhost:8086";
    private static final String INFLUXDB_SERVER_DOC = "The influxdb host and port to connect";

    /**
     * Property name for setting InfluxDB user.
     */
    public static final String INFLUXDB_USER_CONFIG = "kafka.influxdb.metrics.user";
    private static final String INFLUXDB_USER_DEFAULT = "user";
    private static final String INFLUXDB_USER_DOC = "The influxdb user";

    /**
     * Property name for setting InfluxDB password.
     */
    public static final String INFLUXDB_PASSWORD_CONFIG = "kafka.influxdb.metrics.password";
    private static final String INFLUXDB_PASSWORD_DEFAULT = "password";
    private static final String INFLUXDB_PASSWORD_DOC = "The influxdb password";

    /**
     * Property name for setting InfluxDB database name.
     */
    public static final String INFLUXDB_DATABASE_CONFIG = "kafka.influxdb.metrics.database";
    private static final String INFLUXDB_DATABASE_DEFAULT = "database";
    private static final String INFLUXDB_DATABASE_DOC = "The influxdb database";

    /**
     * Property name for setting InfluxDB retention policy.
     */
    public static final String INFLUXDB_RETENTION_POLICY_CONFIG = "kafka.influxdb.metrics.retention.policy";
    private static final String INFLUXDB_RETENTION_POLICY_DEFAULT = "autogen";
    private static final String INFLUXDB_RETENTION_POLICY_DOC = "Influxdb retention policy name";

    /**
     * Property name for setting InfluxDB metrics sending interval.
     */
    public static final String INFLUXDB_INTEVAL_SEC_CONFIG = "kafka.influxdb.metrics.send.interval.secs";
    private static final String INFLUXDB_INTEVAL_SEC_DEFAULT = "60";
    private static final String INFLUXDB_INTERVAL_SEC_DOC = "Metrics sending interval in seconds";

    /**
     * Property name for setting custom tags for InlfuxDB metrics.
     */
    public static final String INFLUXDB_CUSTOM_TAGS_CONFIG = "kafka.influxdb.metrics.custom.tags";
    private static final String INFLUXDB_CUSTOM_TAGS_DEFAULT = "";
    private static final String INFLUXDB_CUSTOM_TAGS_DOC =
            "Map of custom tags to add to all measurements, format key1=value1,key2=value2";

    /**
     * Property name for setting prefix for InfluxDB metric names.
     */
    public static final String INFLUXDB_METRICS_NAME_PREFIX_CONFIG = "kafka.influxdb.metrics.name.prefix";
    private static final String INFLUXDB_METRICS_NAME_PREFIX_DEFAULT = "";
    private static final String INFLUXDB_METRICS_NAME_PREFIX_DOC =
            "Prefix to append to each influxdb measurement with dot as separator";

    /**
     * Property name for setting regular expression "whitelist" for InfluxDB metrics.
     */
    public static final String INFLUXDB_METRICS_WHITELIST_REGEX_CONFIG = "kafka.influxdb.metrics.whitelist.regex";
    private static final String INFLUXDB_METRICS_WHITELIST_REGEX_DEFAULT = "";
    private static final String INFLUXDB_METRICS_WHITELIST_REGEX_DOC =
            "Java regex to pass only metrics with matching names to influxdb";

    /**
     * Property name for setting regular expression "blacklist" for InfluxDB metrics.
     */
    public static final String INFLUXDB_METRICS_BLACKLIST_REGEX_CONFIG = "kafka.influxdb.metrics.blacklist.regex";
    private static final String INFLUXDB_METRICS_BLACKLIST_REGEX_DEFAULT = "";
    private static final String INFLUXDB_METRICS_BLACKLIST_REGEX_DOC =
            "Java regex to do NOT pass metrics with matching names to influxdb";

    private static final ConfigDef CONFIG_DEFINITION = new ConfigDef()
            .define(INFLUXDB_SERVER_CONFIG, STRING, INFLUXDB_SERVER_DEFAULT, HIGH, INFLUXDB_SERVER_DOC)
            .define(INFLUXDB_USER_CONFIG, STRING, INFLUXDB_USER_DEFAULT, MEDIUM, INFLUXDB_USER_DOC)
            .define(INFLUXDB_PASSWORD_CONFIG, PASSWORD, INFLUXDB_PASSWORD_DEFAULT, MEDIUM, INFLUXDB_PASSWORD_DOC)
            .define(INFLUXDB_DATABASE_CONFIG, STRING, INFLUXDB_DATABASE_DEFAULT, MEDIUM, INFLUXDB_DATABASE_DOC)
            .define(INFLUXDB_RETENTION_POLICY_CONFIG, STRING,
                    INFLUXDB_RETENTION_POLICY_DEFAULT, LOW, INFLUXDB_RETENTION_POLICY_DOC)
            .define(INFLUXDB_INTEVAL_SEC_CONFIG, INT, INFLUXDB_INTEVAL_SEC_DEFAULT, LOW, INFLUXDB_INTERVAL_SEC_DOC)
            .define(INFLUXDB_CUSTOM_TAGS_CONFIG, STRING, INFLUXDB_CUSTOM_TAGS_DEFAULT, LOW, INFLUXDB_CUSTOM_TAGS_DOC)
            .define(INFLUXDB_METRICS_NAME_PREFIX_CONFIG, STRING,
                    INFLUXDB_METRICS_NAME_PREFIX_DEFAULT, LOW, INFLUXDB_METRICS_NAME_PREFIX_DOC)
            .define(INFLUXDB_METRICS_WHITELIST_REGEX_CONFIG, STRING,
                    INFLUXDB_METRICS_WHITELIST_REGEX_DEFAULT, LOW, INFLUXDB_METRICS_WHITELIST_REGEX_DOC)
            .define(INFLUXDB_METRICS_BLACKLIST_REGEX_CONFIG, STRING,
                    INFLUXDB_METRICS_BLACKLIST_REGEX_DEFAULT, LOW, INFLUXDB_METRICS_BLACKLIST_REGEX_DOC);

    InfluxdbConfig(Map<?, ?> originals) {
        super(CONFIG_DEFINITION, originals);
    }
}
